(ns com.slothrop.bbref.batting
  (:import [org.jsoup Jsoup]))

(set! *warn-on-reflection* true)

(defn bbref-conn
  {:doc "Opens a JSoup Connection to the URL."}
  ^org.jsoup.Connection [url] (Jsoup/connect url))

(defn doc
  {:doc "Executes a GET request on the open Connection and returns the HTML data as a JSoup Document."}
  [url] (.get (bbref-conn url)))

(defn get-batting-table
  {:doc "Returns the Standard Batting table from a player's BBRef profile."}
  [^org.jsoup.nodes.Document doc]
  (.. doc (getElementById "batting_standard") children))

(defn html->str
  {:doc "Creates a string representation of tabular data provided as a Document."}
  [^org.jsoup.select.Elements doc]
  (->> doc .text (re-seq #"\w+") (partition 32)))

(defn data [d] (-> d doc get-batting-table html->str))

(def str->cols
  "A transducer to filter column names from the raw HTML text string."
  (comp
   (take 1)
   (mapcat identity)
   (filter (complement #{"Standard" "Batting"}))))

(defn cols
  {:doc "Runs the str->cols transducer to extract column names."}
  [doc] (->> doc data (transduce str->cols conj)))

(defn rows
  {:doc "Returns a vector of the table rows."}
  [data] (into [] (eduction (map vec) (drop 1) data)))

(def adley (doc "https://www.baseball-reference.com/players/r/rutscad01.shtml"))

(.selectXpath adley "//table")