(ns tests
  (:require [clojure.test :refer [deftest is run-tests]]
            [html-parsing.http.client :refer [send-split-reqs split-query]])
  (:import [java.time LocalDate]))



(deftest test-multi-req
  (let [s (repeat 3 "https://jsonplaceholder.typicode.com/todos/1")
        result (send-split-reqs s)] (is (= 3 (count result)))
       (is (not-any? #(instance? IllegalArgumentException %) result))))

(deftest test-split-dates
  (let [dates (split-query "2022-05-01" "2022-05-30")]
    (is (= 9 (count dates)))
    (is (= 2 (count (last dates))))
    (is (= (-> dates last first) (LocalDate/of 2022 05 25)))))

(run-tests)