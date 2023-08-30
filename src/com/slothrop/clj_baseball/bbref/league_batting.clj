(ns com.slothrop.clj-baseball.bbref.league-batting
  (:require [com.slothrop.clj-baseball.bbref.datasource :refer [-get]]
            [clojure.string :as string]
            [tech.v3.dataset :as d])
  (:import [org.jsoup.nodes Document Element]
           [org.jsoup.select Elements]))

(set! *warn-on-reflection* false)

(defn get-html [start-date end-date]
  (let [url "http://www.baseball-reference.com/leagues/daily.cgi?user_team=&bust_cache=&type=b&lastndays=7&dates=fromandto&fromandto=%s.%s&level=mlb&franch=&stat=&stat_value=0"]
    (-> url
        (format start-date end-date)
        -get)))

(defn get-table ^Elements [^Document html]
  (.getElementsByTag html "table"))

(def batting (get-html "2023-08-27" "2023-08-28"))

(def headers
  (let [headers (into
                 []
                 (comp
                  (map #(.text ^Element %))
                  (take 29)
                  (remove string/blank?))
                 (-> (get-table batting)
                     (nth 0)
                     (.selectXpath "//table//tr/th")))]
    (assoc headers 0 "mlb-id")))

(defn- get-mlb-id [^Element row]
  (some-> row
          (.select "a")
          first
          .attributes
          (.get "href")
          (string/split #"mlb_ID=")
          (nth 1)))

(defn- get-stats [table]
  (for [row (.select table "tr")
        :let [data (.. row (select "td") text)
              [player-name stats-str] (string/split data #"gl")]
        :when (some? stats-str)]
    (let [stats (-> stats-str
                    string/trim
                    (string/split #" "))]
      (concat [player-name] stats))))

;; almost there
(def batting-stats-table
  (let [table (-> (get-table batting)
                  ^Element (nth 0)
                  (.selectXpath "//table//tbody"))
        ids (-> table
                (.select "tr")
                (->> (into []
                           (map get-mlb-id))))
        data (map (comp
                   #(zipmap headers %)
                   conj)
                  (get-stats table)
                  ids)]
    (d/->dataset data)))

(comment
  batting-stats-table)