(ns com.slothrop.clj-baseball.bbref.league-pitching
  (:require [com.slothrop.clj-baseball.bbref.datasource :refer [-get]]
            [com.slothrop.clj-baseball.utils :refer [most-recent-season]]
            [clojure.string :as string])
  (:import [org.jsoup.nodes Document Element]
           [org.jsoup.select Elements Evaluator$Tag]))

(defn get-html [start-date end-date]
  (assert (some? start-date) "You must provide a start date.")
  (assert (some? end-date) "You must provide an end date.")
  (let [url (format "http://www.baseball-reference.com/leagues/daily.cgi?user_team=&bust_cache=&type=p&lastndays=7&dates=fromandto&fromandto=%s.%s&level=mlb&franch=&stat=&stat_value=0" start-date end-date)
        response (-get url)]
    response))

(defn get-table [^Document html]
  (let [^Element table (.selectFirst html (Evaluator$Tag. "table"))
        headings (into [] (map #(.text ^Element %)) (-> table
                                                        ^Element (.selectFirst (Evaluator$Tag. "tr"))
                                                        (.getElementsByTag "th")
                                                        rest))
        headings (conj headings "mlbID")
        ^Element body (.selectFirst table (Evaluator$Tag. "tbody"))
        ^Elements rows (.getElementsByTag body "tr")]
    (map (partial zipmap headings)
         (for [^Element row rows
               :let [cols (into [] ^Elements (.getElementsByTag row "td"))
                     ^Element row-anchor (.selectFirst row (Evaluator$Tag. "a"))
                     mlb-id (some-> row-anchor
                                    .attributes
                                    (.get "href")
                                    (string/split #"mlbID="))
                     cols (conj cols mlb-id)]]
           cols))))

(def html (get-html "2023-04-01" "2023-04-02"))

(comment (get-table html))