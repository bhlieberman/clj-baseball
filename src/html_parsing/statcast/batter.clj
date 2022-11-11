(ns html-parsing.statcast.batter
  (:require [html-parsing.http.client]
            [clojure.test :refer [deftest is testing run-tests]])
  (:import [org.jsoup Jsoup]
           [java.time LocalDate]))

(def form-data [:pitch-type :pitch-result :batted-ball-location
                :count :player-type :pitcher-handedness :game-date-after
                :team :position :innning :flags :metric-range
                :group-by :min-pa :pa-result :gameday-zones :attack-zones
                :season :outs :batter-handedness :game-date-before
                :home-or-away :if-alignment :batted-ball-type
                :min-pitches :min-results])

(def batter-defaults
  "The default query map. Assumes nil values for all fields except :game-date-before,
   which receives a LocalDate object of today's date, and :player-type taking \"batter\""
  (let [times (count form-data)]
    (-> form-data
        (zipmap (repeat times nil))
        (assoc :player-type "batter"
               :game-date-before (LocalDate/now)))))

(defn send-form
  "This function takes a map of data sent to Statcast for querying"
  [default rest]
  (merge default rest))

(deftest name-test
  (testing "that the client")) 

