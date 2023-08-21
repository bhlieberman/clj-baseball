(ns com.slothrop.player.lookup
  (:require [clojure.java.io :as jio]
            [clj-http.client :as client]
            [tablecloth.api.missing :as m]
            [tablecloth.api.rows :as tcr]
            [tech.v3.dataset.io.csv :as csv]
            [tech.v3.dataset :as d]
            [tech.v3.dataset.join :as j]
            [tech.v3.dataset.zip :as z]
            [com.slothrop.cache.cache-config :refer [DEFAULT-CACHE-DIR]])
  (:import [me.xdrop.fuzzywuzzy FuzzySearch]))

(defn table->csv [table]
  (with-open [is (jio/input-stream table)]
    (csv/csv->dataset is {:file-type :csv})))

(def ^:deprecated lookup-table
  "This will be removed in v0.2.1. Prefer Chadwick Bureau register dataset."
  (-> (client/get "https://clj-baseball.s3.us-west-2.amazonaws.com/lookup-table"
                  {:async? true
                   :as :stream}
                  (fn [response] response)
                  (fn [error] (throw (ex-info "Request for player lookup table failed" {:cause error}))))
      .get
      .getEntity
      .getContent
      table->csv))

;; lookup table v2 from Chadwick Bureau
(def register (client/get "https://github.com/chadwickbureau/register/archive/refs/heads/master.zip"
                          {:async? true
                           :as :stream}
                          (fn [resp] resp)
                          (fn [err] (throw (ex-info "Request for player lookup table failed" {:cause err})))))

(defn get-cached-register-file []
  (if (.exists (jio/file (.toUri DEFAULT-CACHE-DIR)))
    (try (table->csv (str DEFAULT-CACHE-DIR "/last_query.csv"))
         (catch java.io.FileNotFoundException _))
    nil))

(defn register->dataset []
  (let [cached-ds (get-cached-register-file)]
    (when-not cached-ds ;; account for cached file later ... if - else
      (z/zipfile->dataset-seq (.. register get getEntity getContent)))))

(defn keep-cols [ds]
  (try (d/select-columns ds ["name_last" "name_first" "key_mlbam"
                             "key_retro" "key_bbref" "key_fangraphs"
                             "mlb_played_first", "mlb_played_last"])
       (catch Exception _)))

(def PEOPLE_PATTERN (re-pattern ".*people\\-[0-9a-f]\\.csv"))

(defn create-full-names [ds]
  (d/row-map ds (fn [row] {:player-name (str (row "name_first") " " (row "name_last"))})))

(defn get-closest-names [player-last player-first player-table]
  (let [player-names (into [] (:player-name player-table))
        most-similar {:player-name
                      (map #(.getString %) (FuzzySearch/extractTop (str player-first player-last)
                                                                   player-names
                                                                   5))}]
    (j/pd-merge (d/->dataset most-similar) player-table {:on :player-name})))

(defn lookup-table-v2 []
  (letfn [(complete-records [ds] (m/drop-missing
                                  ds
                                  ["key_retro" "key_bbref" "key_fangraphs"
                                   "mlb_played_first" "mlb_played_last"]))]
    (->> (register->dataset)
         (filter (fn [ds]
                   (-> ds
                       d/dataset-name
                       (->> (re-matcher PEOPLE_PATTERN)
                            re-find))))
         (apply d/concat)
         keep-cols
         complete-records
         create-full-names)))

(defn search [{:keys [player-last player-first fuzzy?] :or {fuzzy? false}}]
  (let [lookup (lookup-table-v2)]
    (if fuzzy?
      (get-closest-names player-last player-first lookup)
      (if (nil? player-first)
        (tcr/select-rows lookup (comp #(= % player-last) #(get % "name_last")))
        (tcr/select-rows lookup (comp (fn [[first last]]
                                        (and (= first player-first) (= last player-last)))
                                      (juxt #(get % "name_first")
                                            #(get % "name_last"))))))))

(comment
  (search {:player-last "Ripken"
           :player-first "Cal"}))

(comment (def keep-cols (keep (fn [[& x]] (map (partial nth x)
                                               (conj (into [] (range 1 11)) 15))))))

(defmulti ^:deprecated player-profile "DEPRECATED! USE table-csv instead! Will be removed in v.0.2.1" identity)
(defmethod player-profile :cols [_] (into [] cat (sequence (comp (take 1) keep-cols))))
(defmethod player-profile :rows [_] (transduce (comp (drop 1) keep-cols) conj lookup-table))