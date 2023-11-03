(ns com.slothrop.clj-baseball.player.lookup
  (:require [clojure.java.io :as jio]
            [clj-http.client :as client]
            [tablecloth.api.missing :as m]
            [tablecloth.api.rows :as tcr]
            [tech.v3.dataset.io.csv :as csv]
            [tech.v3.dataset :as d]
            [tech.v3.dataset.join :as j]
            [tech.v3.dataset.zip :as z]
            [com.slothrop.clj-baseball.cache.cache-config :refer [DEFAULT-CACHE-DIR]])
  (:import [me.xdrop.fuzzywuzzy FuzzySearch]
           [me.xdrop.fuzzywuzzy.model BoundExtractedResult]
           [org.apache.http.impl.nio.client FutureWrapper]
           [org.apache.http.message BasicHttpResponse]
           [org.apache.http.nio.entity ContentBufferEntity]))

(set! *warn-on-reflection* true)

(defn- table->csv [table]
  (with-open [is (jio/input-stream table)]
    (csv/csv->dataset is {:file-type :csv})))

(defn- get-cached-register-file []
  (if (.exists (jio/file (.toUri ^java.nio.file.Path DEFAULT-CACHE-DIR)))
    (try (table->csv (str DEFAULT-CACHE-DIR "/last_query.csv"))
         (catch java.io.FileNotFoundException _))
    nil))

(defn ^:private register->dataset []
  (let [^FutureWrapper register (client/get "https://github.com/chadwickbureau/register/archive/refs/heads/master.zip"
                                            {:async? true
                                             :as :stream}
                                            (fn [resp] resp)
                                            (fn [err] (throw (ex-info "Request for player lookup table failed" {:cause err}))))
        cached-ds (get-cached-register-file)
        ^BasicHttpResponse resp (.get register)
        ^ContentBufferEntity contentity (.getEntity resp)]
    (future (or cached-ds (-> contentity
                              .getContent
                              z/zipfile->dataset-seq)))))

(defn- keep-cols [ds]
  (try (d/select-columns ds ["name_last" "name_first" "key_mlbam"
                             "key_retro" "key_bbref" "key_fangraphs"
                             "mlb_played_first", "mlb_played_last"])
       (catch Exception _)))

(def ^:private PEOPLE_PATTERN (re-pattern ".*people\\-[0-9a-f]\\.csv"))

(defn- create-full-names [ds]
  (d/row-map ds (fn [row] {:player-name (str (row "name_first") " " (row "name_last"))})))

(defn- get-closest-names [player-last player-first player-table]
  (let [player-names (into [] (:player-name player-table))
        most-similar {:player-name
                      (map #(.getString ^BoundExtractedResult %) (FuzzySearch/extractTop (str player-first player-last)
                                                                                         player-names
                                                                                         5))}]
    (j/pd-merge (d/->dataset most-similar) player-table {:on :player-name})))

(defn- lookup-table []
  (letfn [(complete-records [ds] (m/drop-missing
                                  ds
                                  ["key_retro" "key_bbref" "key_fangraphs"
                                   "mlb_played_first" "mlb_played_last"]))]
    (->> (register->dataset)
         deref
         (filter (fn [ds]
                   (-> ds
                       d/dataset-name
                       (->> (re-matcher PEOPLE_PATTERN)
                            re-find))))
         (apply d/concat)
         keep-cols
         complete-records
         create-full-names)))

(defn search
  {:doc "Look up a player's MLB ID from the Chadwick Bureau dataset. Accepts a `fuzzy?` flag to enable fuzzy searching of names."}
  [{:keys [player-last player-first fuzzy?] :or {fuzzy? false}}]
  (let [table (lookup-table)]
    (if fuzzy?
      (get-closest-names player-last player-first table)
      (if (nil? player-first)
        (tcr/select-rows table (comp #(= % player-last) #(get % "name_last")))
        (tcr/select-rows table (comp (fn [[first last]]
                                       (and (= first player-first) (= last player-last)))
                                     (juxt #(get % "name_first")
                                           #(get % "name_last"))))))))

(comment
  (search {:player-last "Ripken"
           :player-first "Cal"}))
