(ns com.slothrop.player.lookup
  (:require [clojure.string :as string]
            [clj-http.client :as client]))

(def lookup-table (future (client/get "https://clj-baseball.s3.us-west-2.amazonaws.com/lookup-table")))

(def keep-cols (keep (fn [[& x]] (map (partial nth x)
                                      (conj (into [] (range 1 11)) 15)))))

(defmulti player-profile identity)
(defmethod player-profile :cols [_] (into [] cat (sequence (comp (take 1) keep-cols))))
(defmethod player-profile :rows [_] (transduce (comp (drop 1) keep-cols) conj lookup-table))

(def filtered-columns (map (comp keyword string/lower-case) (player-profile :cols)))
(def rows (player-profile :rows))
(def table (transduce (map (partial zipmap filtered-columns)) conj rows))