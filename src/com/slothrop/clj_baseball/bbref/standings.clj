(ns com.slothrop.clj-baseball.bbref.standings
  (:require [com.slothrop.clj-baseball.bbref.datasource :refer [-get]]
            [clojure.string :as string])
  (:import [org.jsoup Jsoup]
           [org.jsoup.nodes Document]))

(def url "http://www.baseball-reference.com/leagues/MLB/%s-standings.shtml")

(defn get-standings-html [year]
  (-get (format url year)))

(defn get-tables [html season]
  (cond
    (>= season 1969)
    (let [tables (.select html "table")
          eighty-one (when (= 1981 season)
                       (filter (fn [x] (let [id (.attr x "id")]
                                         (string/includes? id "overall"))) tables))]
      (for [table (if (= 1981 season) eighty-one tables)
            :let [headings (.selectXpath table "//tr//th")
                  [team-names stats] #(group-by (fn [coll] (> (count (string/split coll #"\s")) 1)) %)
                  body (.selectXpath table "//tbody//tr")
                  rows (for [row body
                             :let [cols (.select row "td")]]
                         (map #(.text %) cols))]]
        
        #_[(map #(.text %) (distinct headings)) rows]))))

(def season (get-standings-html 1981))

(get-tables season 1981)

(comment 
  (let [headings 
        ["Milwaukee Brewers"
         "Boston Red Sox"
         "Detroit Tigers"
         "Baltimore Orioles"
         "Cleveland Indians"
         "New York Yankees"
         "Toronto Blue Jays"
         "Tm"
         "W"
         "L"
         "W-L%"
         "GB"
         "Kansas City Royals"
         "Oakland Athletics"
         "Texas Rangers"
         "Minnesota Twins"
         "Seattle Mariners"
         "Chicago White Sox"
         "California Angels"
         "Tm"
         "W"]]
    (group-by (fn [el] (> (count (string/split el #"\s")) 1)) (into [] (comp (distinct)) headings))))
