(ns com.slothrop.clj-baseball.bbref.league-batting
  (:require [com.slothrop.clj-baseball.bbref.datasource :refer [-get]]
            [clojure.string :as string] 
            [tech.v3.dataset :as d])
  (:import [org.jsoup.nodes Document Element]
           [org.jsoup.select Elements]))

(set! *warn-on-reflection* false)

(defn- get-html [start-date end-date]
  (let [url "http://www.baseball-reference.com/leagues/daily.cgi?user_team=&bust_cache=&type=b&lastndays=7&dates=fromandto&fromandto=%s.%s&level=mlb&franch=&stat=&stat_value=0"]
    (-> url
        (format start-date end-date)
        ;; BUG: -get times out on first call: evaluate again and it works
        -get)))

(defn- get-table ^Elements [^Document html]
  (nth (.getElementsByTag html "table") 0))

(def ^:private text (map #(.text ^Element %)))

(defn- headers [table]
  (let [xf (comp text (take 29) (remove string/blank?))
        headers (into [] xf (.selectXpath table "//table//tr//th"))]
    (assoc headers 0 "MLB_ID")))

(defn batting-stats-table
  {:doc "Retrieves daily batting leaders for the provided date range."}
  [start-date end-date]
  (let [table (get-table (get-html start-date end-date))
        headers (headers table)
        body (.selectXpath table "//table//tbody//tr//td")
        xf (comp (partition-all 28)
                 (map (fn [[[id name] _ & stats]] (zipmap headers
                                                          (apply vector id name stats)))))]
    (d/->dataset (into [] xf
                       (for [td body
                             :let [mlb-id (some-> td
                                                  (.select "a[href]")
                                                  first
                                                  .attributes
                                                  (.get "href")
                                                  (string/split #"mlb_ID=")
                                                  (nth 1 nil))
                                   id-or-stats (if (some? mlb-id) [mlb-id (.text td)] (.text td))]]
                         id-or-stats)))))

(comment
  (batting-stats-table "2023-04-28" "2023-05-01"))