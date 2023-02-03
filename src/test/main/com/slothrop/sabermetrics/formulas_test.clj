(ns test.main.com.slothrop.sabermetrics.formulas-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [clojure.spec.alpha :as s]
            [com.slothrop.sabermetrics.formulas :as formulas]))

(def test-data
  #:com.slothrop.sabermetrics.formulas{:ab 608
                                       :ba 0.258
                                       :obp 0.318
                                       :slg 0.403
                                       :ops 0.721
                                       :bb 47
                                       :h 157
                                       :pa 672
                                       :hbp 9
                                       :k 126
                                       :sf 5})

(deftest batting-avg-test
  (testing "that the batting average computation is correct"
    (is (< 0.258 (formulas/batting-avg test-data) 0.259))))

(deftest k-rate-test
  (testing "that the strikeout rate computation is correct"
    (is (float? (formulas/k-rate test-data)))))

(deftest walk-outcome-test
  (testing "that the walk outcome multimethod returns a map"
    (let [event-map (assoc test-data :outcome/type :outcome/walk)]
      (is (instance? clojure.lang.IPersistentMap
                     (formulas/at-bat-outcome event-map)))
      (is (->> event-map
               formulas/at-bat-outcome
               #(juxt [:walk-rate :bb])
               (fn [[walk-rate bb]] (and (float? bb)
                                         (= walk-rate 48))))))))

(deftest exception-throws-on-spec-fail-test
  (testing "that an event failing spec will cause an exception to be thrown"
    (let [event-map (-> test-data
                        (assoc :outcome/type :outcome/walk)
                        (dissoc :com.slothrop.sabermetrics.formulas/bb))]
      (is (= "failed spec"
           (try (formulas/dispatch-f (s/conform :outcome/outcome event-map))
                (catch clojure.lang.ExceptionInfo e
                  (-> e ex-data :cause))))))))

(deftest add-walk-rate-test
  (testing "that the walk rate is assoc'd into the map of stats"
    (let [event-map (assoc test-data :outcome/type :outcome/walk)]
      (is (-> event-map formulas/at-bat-outcome :walk-rate some?)))))

(run-tests)