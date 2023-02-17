(ns com.slothrop.player.lookup
  (:require [charred.api :refer [read-csv]]
            [clojure.string :as string]
            [clj-http.client :as client]))

(def lookup-table
  (->>
   (client/get "https://clj-baseball.s3.us-west-2.amazonaws.com/lookup-table")
   :body
   read-csv))

(def keep-cols (keep (fn [[& x]] (map (partial nth x)
                                      (conj (into [] (range 1 11)) 15)))))

(defmulti player-profile identity)
(defmethod player-profile :cols [_] (into [] cat (sequence (comp (take 1) keep-cols) lookup-table)))
(defmethod player-profile :rows [_] (transduce (comp (drop 1) keep-cols) conj lookup-table))

(def filtered-columns (map (comp keyword string/lower-case) (player-profile :cols)))
(def rows (player-profile :rows))
(def table (transduce (map (partial zipmap filtered-columns)) conj rows))

(:brefid (first table))