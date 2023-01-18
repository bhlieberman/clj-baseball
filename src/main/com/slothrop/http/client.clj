(ns com.slothrop.http.client 
  (:require [clojure.core.async :refer [go chan alts!! >!]]
            #_[clojure.spec.alpha :as s]
            [clj-http.client :as client] 
            [ring.util.response :refer [response]]))

(defn send-split-reqs ^{:deprecated true} [reqs]
  (let [c (count reqs) cs (repeatedly c chan)]
    (doseq [req reqs]
      (go (>! (rand-nth cs)
              (client/get req))))
    (loop [i 0 result []]
      (if (< i c)
        (let [[v _] (alts!! cs)]
          (recur (inc i) (conj result v)))
        result))))