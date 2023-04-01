(ns test.main.com.slothrop.statcast.batter-spec-test
  (:require [clojure.test :refer [deftest is run-test run-tests]]
            [clojure.spec.alpha :as s]
            [com.slothrop.statcast.specs :as-alias stats]
            [com.slothrop.statcast.batter :refer [query-defaults make-query-map]]))

(deftest build-team-sets
  (is (s/valid? ::stats/team #{:orioles :yankees})))

(deftest build-query-map
  (is (s/valid? ::stats/query query-defaults)))

(deftest test-game-dates
  (is (and (s/valid? ::stats/game-date-lt (java.time.LocalDate/now))
           (s/valid? ::stats/game-date-lt (str (java.time.LocalDate/now))))))

(deftest make-query-map-test
  (let [test-query-map-1 {:hfTeam "BAL" :season #{:2019} :outs #{:0 :1}}]
    (is (s/valid? ::stats/query (make-query-map query-defaults test-query-map-1)))))

(run-tests)