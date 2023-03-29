(ns test.main.com.slothrop.statcast.batter-test
  (:require [clojure.test :refer [deftest is run-tests]]
            [com.slothrop.statcast.batter :refer [send-req!]]))

(deftest send-req-does-not-timeout
  (is (not= (count (send-req! {:game-date-gt "2021-05-01" :game-date-lt "2021-05-01"})) 2)))

(run-tests)