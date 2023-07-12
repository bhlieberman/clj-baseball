(ns resources.dev-user
  #_{:clj-kondo/ignore [:unused-namespace]}
  (:require [portal.api :as p]
            [clojure.spec.alpha :as s]
            [tech.v3.dataset :as d]
            [com.slothrop.statcast.batter :refer [send-req!]]
            [com.slothrop.statcast.results-spec :as rspec]
            [com.slothrop.bbref.batting :refer [data rows]]
            [com.slothrop.player.lookup :refer [lookup-player-by-mlbid table-csv]])
  (:import [java.net URLEncoder]
           [java.util.zip ZipFile]))


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