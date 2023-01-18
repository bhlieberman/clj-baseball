(ns main.com.slothrop.utils.dates
  (:require [clojure.spec.alpha :as s])
  (:import [java.time LocalDate]
           [java.time.format DateTimeFormatter]))

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