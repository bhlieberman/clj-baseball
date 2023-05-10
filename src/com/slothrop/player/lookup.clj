(ns com.slothrop.player.lookup
  (:require [clojure.java.io :as jio]
            [clj-http.client :as client]
            [tech.v3.dataset.io.csv :as csv]
            [tech.v3.dataset :as d]))

(defn table-csv [table]
  (with-open [is (jio/input-stream table)]
    (csv/csv->dataset is {:file-type :csv})))

(def lookup-table (-> (client/get "https://clj-baseball.s3.us-west-2.amazonaws.com/lookup-table"
                               {:async? true
                                :as :stream}
                               (fn [response] response)
                               (fn [error] (throw (ex-info "Request for player lookup table failed" {:cause error}))))
                      .get
                      .getEntity
                      .getContent
                      table-csv))

(defn lookup-player-by-mlbid [id]
  (d/filter-column lookup-table "MLBID" id))

(def keep-cols (keep (fn [[& x]] (map (partial nth x)
                                      (conj (into [] (range 1 11)) 15)))))

(defmulti ^:deprecated player-profile "DEPRECATED! USE table-csv instead!" identity)
(defmethod player-profile :cols [_] (into [] cat (sequence (comp (take 1) keep-cols))))
(defmethod player-profile :rows [_] (transduce (comp (drop 1) keep-cols) conj lookup-table))