(ns com.slothrop.player.lookup
  (:require [clojure.java.io :as jio]
            [clj-http.client :as client]
            [tech.v3.dataset.io.csv :as csv]
            [tech.v3.dataset :as d]
            [tech.v3.dataset.zip :as z]
            [com.slothrop.cache.cache-config :refer [DEFAULT-CACHE-DIR]]))

(defn table-csv [table]
  (with-open [is (jio/input-stream table)]
    (csv/csv->dataset is {:file-type :csv})))

(def ^:deprecated lookup-table
  "This will be removed in v0.2.0. Prefer Chadwick Bureau register dataset."
  (-> (client/get "https://clj-baseball.s3.us-west-2.amazonaws.com/lookup-table"
                  {:async? true
                   :as :stream}
                  (fn [response] response)
                  (fn [error] (throw (ex-info "Request for player lookup table failed" {:cause error}))))
      .get
      .getEntity
      .getContent
      table-csv))

;; lookup table v2 from Chadwick Bureau
(def register (client/get "https://github.com/chadwickbureau/register/archive/refs/heads/master.zip"
                          {:async? true
                           :as :stream}
                          (fn [resp] resp)
                          (fn [err] (throw (ex-info "Request for player lookup table failed" {:cause err})))))

(defn get-cached-register-file []
  (if (.exists (jio/file (.toUri DEFAULT-CACHE-DIR)))
    (try (table-csv (str DEFAULT-CACHE-DIR "/last_query.csv"))
         (catch java.io.FileNotFoundException _))
    nil))

(defn register->dataset []
  (let [cached-ds (get-cached-register-file)]
    (when-not cached-ds
      (z/zipfile->dataset-seq (.. register get getEntity getContent)))))

(defn keep-cols [ds]
  (try (d/select-columns ds ["name_last" "name_first" "key_mlbam"
                             "key_retro" "key_bbref" "key_fangraphs"
                             "mlb_played_first", "mlb_played_last"])
       (catch Exception _)))

(def PEOPLE_PATTERN (re-pattern ".*people\\-[0-9a-f]\\.csv"))

(defn create-full-names [ds]
  (d/row-map ds (fn [row] {:player_name (str (row "name_first") " " (row "name_last"))})))

(defn lookup-table-v2 []
  (->> (register->dataset)
       (filter (fn [ds]
                 (-> ds
                     d/dataset-name
                     (->> (re-matcher PEOPLE_PATTERN)
                          re-find))))
       (apply d/concat)
       keep-cols
       create-full-names))

(defn lookup-player-by-mlbid [id]
  (d/filter-column (lookup-table-v2) "MLBID" id))

(comment (tap> (d/select-rows (lookup-table-v2) 0)))

(comment (d/head (lookup-table-v2) 2))

(comment (def keep-cols (keep (fn [[& x]] (map (partial nth x)
                                               (conj (into [] (range 1 11)) 15))))))

(defmulti ^:deprecated player-profile "DEPRECATED! USE table-csv instead!" identity)
(defmethod player-profile :cols [_] (into [] cat (sequence (comp (take 1) keep-cols))))
(defmethod player-profile :rows [_] (transduce (comp (drop 1) keep-cols) conj lookup-table))