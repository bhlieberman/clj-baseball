(ns statcast.batter-test
  (:require [clojure.test :refer [deftest is run-test run-tests]]
            [clojure.spec.alpha :as s]
            [com.slothrop.statcast.specs :as-alias stats]
            [com.slothrop.statcast.batter :refer [query-defaults make-query-map]]))

(deftest build-team-sets
  (is (= true (s/valid? ::stats/team #{:orioles :yankees}))))

(deftest build-query-map
  (is (= (identity query-defaults) (s/conform ::stats/query query-defaults))))

(deftest test-game-dates
  (is (and (s/valid? ::stats/game-date-lt (java.time.LocalDate/now))
           (s/valid? ::stats/game-date-lt (str (java.time.LocalDate/now))))))

(s/explain ::stats/query query-defaults)

(run-tests)