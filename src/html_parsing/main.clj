(ns html-parsing.main
  (:require [table.core :as t]
   [clojure.java.io :as io])
  (:import [org.jsoup Jsoup]
           [java.io File]))

(def html-conn (Jsoup/connect "https://www.baseball-reference.com/players/m/mullice01.shtml"))

(def doc (.get html-conn))

(def cedric (-> doc (.getElementsByTag "table") first .children .text))

(defn html->str [doc] (->> doc (re-seq #"\w+") (partition 32)))

(def data (html->str cedric))

(def str->cols (comp
                (take 1)
                (mapcat identity)
                (filter (complement #{"Standard" "Batting"}))))

(def cols (transduce str->cols conj data))

(def rows (into [] (eduction (map vec) (drop 1) data)))

(let [f (File. "test-table.txt")]
  (with-open [wtr (io/writer f)]
    (.write wtr (t/table-str [cols rows] :style :unicode))))