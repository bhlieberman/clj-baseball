(ns test.main.com.slothrop.statcast.utils-test
  (:require [clojure.test :refer [deftest is run-tests]]
            [com.slothrop.statcast.utils :refer [transform-existing-query-vals transform-vals]]
            [com.slothrop.statcast.batter :refer [query-defaults]]))

(deftest transform-existing-query-vals-test
  (let [test-query-map-1 {:hfTeam "BAL"}
        test-query-map-2 {:hfTeam ["NYY"] :hfSea nil}
        test-query-map-3 {:hfPT #{"CUKC"}}]
    (is (not (instance? java.lang.Throwable
                        (merge-with transform-existing-query-vals query-defaults test-query-map-1))))
    (let [res2 (merge-with transform-existing-query-vals query-defaults test-query-map-2)
          res3 (merge-with transform-existing-query-vals query-defaults test-query-map-3)]
      (is (and (= ["NYY"] (:hfTeam res2))
              (nil? (:hfSea res2))))
      (is (= #{"CUKC"} (:hfPT res3))))))

(deftest transform-vals-test
  (let [test-query-map-1 {:hfSea "R|"}]
    (is (not (instance? java.lang.Throwable (transform-vals test-query-map-1))))))

(run-tests)