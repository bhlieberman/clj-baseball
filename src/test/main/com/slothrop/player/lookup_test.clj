(ns player.lookup-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [tech.v3.dataset :as d]
            [com.slothrop.player.lookup :refer [lookup-table-v2
                                                get-cached-register-file]]))

(deftest cached-file-not-exists
  (is (nil? (get-cached-register-file))))

(deftest lookup-table-pipeline
  (testing "that the full pipeline of functions correctly reduces and filters the Chadwick dataset"
    (let [register (lookup-table-v2)]
      (is (some? register))
      (is (= (count (d/rows register)) 20627)))))

(run-tests)