(ns html-parsing.main 
  (:import [org.jsoup Jsoup]))

(defn bbref-conn [url] (Jsoup/connect url))

(def doc (.get bbref-conn))

(defn get-batting-table [doc] 
  (some-> doc 
      (.getElementById "batting_standard")
      .children))

(defn html->str [doc] (->> doc .text (re-seq #"\w+") (partition 32)))

(defn data [doc] (-> doc get-batting-table html->str))

(def str->cols (comp
                (take 1)
                (mapcat identity)
                (filter (complement #{"Standard" "Batting"}))))

(def cols (->> doc data (transduce str->cols conj)))

(def rows (into [] (eduction (map vec) (drop 1) data)))
