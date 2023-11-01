(ns com.slothrop.clj-baseball.bbref.standings
  (:require [com.slothrop.clj-baseball.bbref.datasource :refer [-get]]
            [clojure.string :as string])
  (:import [org.jsoup.nodes Document]))

(def url "http://www.baseball-reference.com/leagues/MLB/%s-standings.shtml")

(defn get-standings-html ^Document
  [year]
  (-get (format url year)))

(defn get-tables [html season]
  (cond
    (>= season 1969)
    (let [tables (cond->> (.select html "table")
                   (= 1981 season)
                   (filter (fn [x] (let [id (.attr x "id")]
                                     (string/includes? id "overall"))))
                   true identity)]
      (for [table tables
            :let [headings (map #(.text %)
                                (-> table
                                    (.getElementsByTag "tr")
                                    first
                                    (.getElementsByTag "th")))
                  body (first (.getElementsByTag table "tbody"))]]
        (into [headings]
              (for [row (.getElementsByTag body "tr")
                    :let [cols (.getElementsByTag row "td")
                          col-text (map (comp string/trim #(.text %)) cols)]]
                (cons (-> row
                          (.getElementsByTag "a")
                          first
                          .text
                          string/trim) col-text)))))
    :else (throw (ex-info "this is unimplemented for now, sorry!" {}))))

(def season (get-standings-html 1900))

(get-tables season 1900)


