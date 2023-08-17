(ns player.lookup-test
  (:require [clojure.test :refer [deftest is run-tests]]
            [com.slothrop.player.lookup :refer [lookup-table lookup-table-v2 lookup-player-by-mlbid
                                                get-cached-register-file]]))

(deftest cached-file-not-exists
  (is (nil? (get-cached-register-file))))

(deftest lookup-v2-tests
  (let [lookup-table (lookup-table-v2)]
    (is (some? lookup-table))))

(run-tests)