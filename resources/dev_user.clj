(ns resources.dev-user
  (:require [portal.api :as p]
            [clojure.spec.alpha :as s]
            #_[com.slothrop.interop.dataframes :refer [create-dataframe-from-dict
                                                       create-dataframe-from-records]]
            [com.slothrop.statcast.batter :refer [send-req!]]
            [com.slothrop.statcast.results-spec :as rspec]
            [com.slothrop.bbref.batting :refer [data rows]]
            [com.slothrop.player.lookup :refer [table]])
  (:import [java.net URLEncoder]))

(def p (p/open {:launcher :vs-code}))
(add-tap #'p/submit)

(def results (send-req! {:game-date-gt "2022-05-01"
                         :game-date-lt "2022-05-02"
                         :hfTeam (URLEncoder/encode "BAL|" "utf-8")}))

(def test-map (first results))

(tap> test-map)

(s/explain ::rspec/results test-map)

#_(create-dataframe-from-records (take 20 results))

(defn run-bbref-query! [{:keys [brefid]}] 
  (let [initial (-> brefid name first)
        url (str "https://www.baseball-reference.com/players/" initial "/" brefid ".shtml")]
   (tap> (rows (data url)))))

(run-bbref-query! (first table))