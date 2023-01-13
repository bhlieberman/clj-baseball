(ns http.client
  (:refer-clojure :exclude [reduce])
  (:require [clojure.core.async :refer [go chan alts!! >! reduce]] 
            [clj-http.client :as client]
            #_[clojure.java.io :as io]
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

(defn split-query [date-start date-end]
  (let [format (DateTimeFormatter/ofPattern "yyyy-MM-dd")
        ds (-> (LocalDate/parse date-start format)
               (.datesUntil (LocalDate/parse date-end format))
               .iterator
               iterator-seq)
        split-ds (if (even? (count ds)) (partition 2 ds) (partition 3 ds))]
    (map (juxt first last) split-ds)))

(comment 
  (let [s (repeat 3 "https://jsonplaceholder.typicode.com/todos/1")
        results (map (comp :body response) (send-split-reqs s))]
    (tap> results))
  
  (tap> (split-query "2022-05-01" "2022-05-30"))
  )
