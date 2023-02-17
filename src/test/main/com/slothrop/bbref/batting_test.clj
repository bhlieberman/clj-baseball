(ns bbref.batting-test
  (:require [clojure.test :refer [deftest is run-tests]]
            [com.slothrop.bbref.batting :as html]))

(def url "https://www.baseball-reference.com/players/m/mullice01.shtml")

(deftest open-connection-test
  (let [conn (html/bbref-conn url)]
    (is (instance? org.jsoup.Connection conn))))

(deftest doc-test
  (is (= 12 (-> url 
                html/doc 
                html/get-batting-table 
                html/html->str 
                count))))

(deftest data-test
  (let [data (html/cols url)]
    (is (= 30 (count data)))))

(run-tests)