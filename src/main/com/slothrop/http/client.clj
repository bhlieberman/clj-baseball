(ns com.slothrop.http.client 
  (:require [clojure.core.async :refer [go chan alts!! >!]]
            [clojure.spec.alpha :as s]
            [clj-http.client :as client] 
            [ring.util.response :refer [response]])
  (:import [java.time LocalDate]
           [java.time.format DateTimeFormatter]))

(defn send-split-reqs [reqs]
  (let [c (count reqs) cs (repeatedly c chan)]
    (doseq [req reqs]
      (go (>! (rand-nth cs)
              (client/get req))))
    (loop [i 0 result []]
      (if (< i c)
        (let [[v _] (alts!! cs)]
          (recur (inc i) (conj result v)))
        result))))

(defn split-dates [date-start date-end]
  {:pre [(s/valid? string? date-start) (s/valid? string? date-end)]
   :post [(s/valid? (s/coll-of (s/map-of #{:game-date-gt :game-date-lt} 
                                #(instance? LocalDate %))) %)]}
  (let [format (DateTimeFormatter/ofPattern "yyyy-MM-dd")
        ds (-> (LocalDate/parse date-start format)
               (.datesUntil (LocalDate/parse date-end format))
               .iterator
               iterator-seq)
        split-ds (if (even? (count ds)) (partition 2 ds) (partition 3 ds))]
    (map (fn [d] (assoc {} :game-date-gt (first d)
                        :game-date-lt (second d))) split-ds)))