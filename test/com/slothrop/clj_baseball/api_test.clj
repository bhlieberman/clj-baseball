(ns com.slothrop.clj-baseball.api-test
  (:require [clojure.test :refer [deftest is testing]]
            [com.slothrop.clj-baseball.api :as api]))

(deftest statcast-returns-data
  (testing "that the api/statcast function returns data or nil"
    (is (some? (api/statcast {:hfTeam "BAL"
                              :game_date_gt "2023-06-01"
                              :game_date_lt "2023-06-02"})))))

(deftest api-no-timeouts
  (testing "that the API functions which rely on the -get method don't timeout"
    (is (seq (api/league-batting "2023-05-02" "2023-05-03")))))

(deftest player-lookup
  (testing "that the core function `mlb-player-id-lookup` returns a MLBID for
            an active player."
    (let [gunnar (api/mlb-player-id-lookup {:player-first "Gunnar" :player-last "Henderson"})]
      (is (some? gunnar)))))

(deftest test-team-batting
  (testing "the `team-batting` API function"
    (let [res (api/team-batting "BAL" 2024 nil)]
      (is (some? res)))))

(deftest test-team-pitching
  (testing "the `team-pitching` API function"
    (let [res (api/team-pitching "PIT" 2024 nil)]
      (is (some? (get res "Pos"))))))

(deftest test-team-fielding
  (testing "the `team-fielding` API function"
    (let [res (api/team-fielding "BAL" 2024 nil)]
      (is (some? res)))))