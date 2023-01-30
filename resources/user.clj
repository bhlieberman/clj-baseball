(ns resources.user 
  (:require [portal.api :as p]
            [clojure.spec.alpha :as s]
            [com.slothrop.interop.dataframes :refer [create-dataframe-from-dict
                                                     create-dataframe-from-records]]
            [com.slothrop.statcast.batter :refer [send-req!]]
            [com.slothrop.statcast.results-spec :as rspec])
  (:import [java.net URLEncoder]))

(def p (p/open {:launcher :vs-code}))
(add-tap #'p/submit)

(def results (send-req! {:game-date-gt "2022-05-01"
                        :game-date-lt "2022-05-02"
                        :hfTeam (URLEncoder/encode "BAL|" "utf-8")}))

(def test-map (first results))

(create-dataframe-from-records (take 20 results))

