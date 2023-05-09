(ns resources.dev-user
  (:require #_[portal.api :as p]
   #_[com.slothrop.interop.dataframes :refer [create-dataframe-from-dict
                                              create-dataframe-from-records]]
   [com.slothrop.statcast.batter :refer [send-req!]]
            [com.slothrop.player.lookup :refer [table-csv]]
            [tech.v3.dataset :as d]))

#_(def p (p/open {:launcher :vs-code}))
#_(add-tap #'p/submit)

(def results (send-req! {:game-date-gt "2022-05-01"
                         :game-date-lt "2022-05-02"
                         :hfTeam "BAL|"}))

(def test-map (first results))

#_(defn run-bbref-query! [{:keys [brefid]}]
    (let [initial (-> brefid name first)
          url (str "https://www.baseball-reference.com/players/" initial "/" brefid ".shtml")]
      (tap> (rows (data url)))))

#_(run-bbref-query! (first table))

(def table-data (d/->dataset results))

(d/descriptive-stats table-data)

(defn lookup-player-by-mlbid [id]
  (let [table (table-csv)]
    (d/filter-column table "MLBID" id)))

(-> table-data 
    (d/filter-column :events (fn [event] (= event "double")))
    :pitcher
    first
    lookup-player-by-mlbid)