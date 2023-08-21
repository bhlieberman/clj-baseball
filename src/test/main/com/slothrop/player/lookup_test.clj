(ns player.lookup-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [tech.v3.dataset :as d]
            [com.slothrop.player.lookup :refer [lookup-table]]))

(deftest lookup-table-pipeline
  (testing "that the full pipeline of functions correctly reduces and filters the Chadwick dataset" 
    (is (some? lookup-table))
    (is (= (count (d/rows lookup-table)) 20627))))

(run-tests)