(ns resources.user 
  (:require [portal.api :as p] 
            [com.slothrop.interop.py-config :refer [create-dataframe]]
            [com.slothrop.statcast.batter :refer [send-req]])
  (:import [java.net URLEncoder]))

(def p (p/open {:launcher :vs-code}))
(add-tap #'p/submit)

(def results (send-req {:game-date-gt "2022-05-01"
                        :game-date-lt "2022-05-02"
                        :hfTeam (URLEncoder/encode "BAL|" "utf-8")}))

(create-dataframe results)