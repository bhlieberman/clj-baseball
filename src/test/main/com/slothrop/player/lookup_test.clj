(ns player.lookup-test
  (:require [clojure.test :refer [deftest is run-tests]]
            [com.slothrop.player.lookup :refer [lookup-table]]
            [charred.api :refer [read-csv]]))

(deftest lookup-table-test
  (is (= 3069 (count (read-csv (:body @lookup-table))))))

(run-tests)