(ns resources.dev-user
  #_{:clj-kondo/ignore [:unused-namespace]}

  (:require #_[portal.api :as p]
   #_[com.slothrop.interop.dataframes :refer #_{:clj-kondo/ignore [:unused-referred-var]}
      [create-dataframe-from-dict
       create-dataframe-from-records]]

   [com.slothrop.statcast.batter :refer [send-req!]]
            [com.slothrop.player.lookup :refer
             #_{:clj-kondo/ignore [:unused-referred-var]}
             [lookup-player-by-mlbid table-csv]]
            [tech.v3.dataset :as d])
  (:import [org.jsoup Jsoup]))

(def results (send-req! {:game-date-gt "2022-05-01"
                         :game-date-lt "2022-05-02"
                         :hfTeam "BAL|"}))

(def table-data (d/->dataset results))

(d/descriptive-stats table-data)

(-> table-data
    (d/filter-column :events (fn [event] (= event "double")))
    :pitcher
    first
    lookup-player-by-mlbid)

(comment (def p (p/open {:launcher :vs-code}))
         (add-tap #'p/submit))