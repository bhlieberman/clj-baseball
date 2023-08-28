(ns com.slothrop.clj-baseball.bbref.league-batting
  (:require [com.slothrop.clj-baseball.bbref.datasource :refer [-get]]))

(defn format-url [start-date end-date]
  (let [url "http://www.baseball-reference.com/leagues/daily.cgi?user_team=&bust_cache=&type=b&lastndays=7&dates=fromandto&fromandto=%s.%s&level=mlb&franch=&stat=&stat_value=0"]
    (-> url
        (format start-date end-date)
        -get)))

(comment
  (format-url "2023-08-27" "2023-08-28"))