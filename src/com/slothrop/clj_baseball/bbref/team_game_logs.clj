(ns ^:no-doc com.slothrop.clj-baseball.bbref.team-game-logs
  (:require [com.slothrop.clj-baseball.bbref.datasource :refer [-get]])
  (:import [org.jsoup.nodes Document$OutputSettings
            Document$OutputSettings$Syntax]
           [org.jsoup.select Evaluator$Tag]))

(defonce url "https://www.baseball-reference.com/teams/tgl.cgi?team=%s&t=%s&year=%s")

(defn team-game-logs [season team log-type]
  (let [t-param (if (= log-type "batting") "b" "p")
        content (-> url
                    (format team t-param season)
                    -get
                    (as-> doc
                          (.outputSettings doc
                                           (.syntax (Document$OutputSettings.) 
                                                    Document$OutputSettings$Syntax/xml))))
        table-id (format "team_%s_gamelogs" log-type)
        table (.getElementById content table-id)]
    (if-not table
      (throw (ex-info "Table with expected ID not found on page." {}))
      table)))

(comment (team-game-logs 2002 "BAL" "b"))