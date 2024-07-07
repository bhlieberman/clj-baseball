(ns com.slothrop.clj-baseball.bbref.league-pitching
  (:require [com.slothrop.clj-baseball.bbref.datasource :refer [-get]]
            [clojure.string :as string]
            [tech.v3.dataset :as d])
  (:import [org.jsoup.nodes Document Element]
           [org.jsoup.select Elements]))

(set! *warn-on-reflection* true)

(defn- get-html [start-date end-date]
  (let [url "http://www.baseball-reference.com/leagues/daily.cgi?user_team=&bust_cache=&type=p&lastndays=7&dates=fromandto&fromandto=%s.%s&level=mlb&franch=&stat=&stat_value=0"]
    (-> url
        (format start-date end-date)
        -get)))

(defn- get-table ^Elements [^Document html]
  (nth (.getElementsByTag html "table") 0))

(def ^:private text (map #(.text ^Element %)))

(defn- headers [table]
  (let [xf (comp text (take 29) (remove string/blank?))
        headers (into [] xf (.selectXpath table "//table//tr//th"))]
    (assoc headers 0 "MLB_ID")))

(defn pitching-stats-table
  {:doc "Retrieves daily pitching leaders for the provided date range."}
  [start-date end-date]
  (let [table (-> (get-html start-date end-date)
                  get-table)
        headers (headers table)
        body (.selectXpath table "//table//tbody/tr/td")
        xf (comp (partition-all 41)
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
