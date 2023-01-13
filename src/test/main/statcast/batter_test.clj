(ns test.main.statcast.batter-test
  (:require [clojure.test :refer [deftest is run-test]]
   [com.slothrop.statcast.batter :refer [replace-default-vals query-defaults make-query-map]]))

(deftest make-query-map-test
  (is (= 3 (count (make-query-map {} 
                                {:date-start? "2022-05-01" 
                                 :date-end? "2022-05-30" 
                                 :team? "BAL"})))))

(run-test make-query-map-test)

(deftest test-update-query-map
  (let [curr (count (replace-default-vals {} query-defaults))
        new-vals (count (replace-default-vals {"hfTeam" "hfTeam=BAL%7C"} query-defaults))]
    (is (= new-vals (+ 7 curr)))))

(deftest test-update-query-map-too
  (let [to-update {"game_date_gt" nil
                   "game_date_lt" nil
                   "hfTeam" "BAL%7C"}
        new-qs (-> to-update
                   (assoc "game_date_gt" "game_date_gt=2022-05-01" "game_date_lt" "game_date_lt=2022-07-01")
                   (replace-default-vals query-defaults))]
    (is (= 2 (count (re-seq #"game_date_\w{2}=\d{4}[-\d{2}]+" new-qs))))))