(ns com.slothrop.clj-baseball.api-test
  (:require [clojure.test :refer [deftest is testing]]
            [com.slothrop.clj-baseball.api :as api]))

(deftest statcast-returns-data
  (testing "that the api/statcast function returns data or nil"
    (is (nil? (api/statcast {:hfTeam "BAL"
                             :game-date-gt "2023-06-01"
                             :game-date-lt "2023-06-02"})))))

(deftest api-no-timeouts
  (testing "that the API functions which rely on the -get method don't timeout"
    (is (seq (api/league-batting "2023-05-02" "2023-05-03")))))