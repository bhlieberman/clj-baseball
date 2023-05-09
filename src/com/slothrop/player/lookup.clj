(ns com.slothrop.player.lookup
  (:require [clojure.java.io :as jio]
            [clj-http.client :as client]
            [tech.v3.dataset.io.csv :as csv]
            [tech.v3.dataset :as d]))

(def lookup-table (future (client/get "https://clj-baseball.s3.us-west-2.amazonaws.com/lookup-table")))

(defn table-csv []
  (with-open [is (jio/input-stream (.getBytes ^String (:body @lookup-table)))]
    (csv/csv->dataset is {:file-type :csv})))

(defn lookup-player-by-mlbid [id]
  (let [table (table-csv)]
    (d/filter-column table "MLBID" id)))

(def keep-cols (keep (fn [[& x]] (map (partial nth x)
                                      (conj (into [] (range 1 11)) 15)))))

(defmulti ^:deprecated player-profile "DEPRECATED! USE table-csv instead!" identity)
(defmethod player-profile :cols [_] (into [] cat (sequence (comp (take 1) keep-cols))))
(defmethod player-profile :rows [_] (transduce (comp (drop 1) keep-cols) conj lookup-table))