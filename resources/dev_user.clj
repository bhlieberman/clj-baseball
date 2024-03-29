(ns resources.dev-user
  #_{:clj-kondo/ignore [:unused-namespace
                        :unused-referred-var]}
  (:require [portal.api :as p]
            [clojure.spec.alpha :as s]
            [clojure.repl :refer [doc]]
            [clojure.java.javadoc :refer [javadoc]]
            [clojure.java.io :as jio]
            [clojure.string :as string]
            [clj-http.client :as client]
            [tablecloth.api :as tc]
            [tablecloth.api.missing :as m]
            [tech.v3.dataset :as d]
            [tech.v3.dataset.column-filters :as cf]
            [tech.v3.dataset.join :as j]
            [tech.v3.dataset.zip :as z]
            [tech.v3.dataset.io.csv :as csv]
            [com.slothrop.clj-baseball.statcast.batter :refer [send-req!]]
           #_[com.slothrop.statcast.results-spec :as-alias rspec]
            #_[com.slothrop.bbref.batting :refer [data rows]]
            #_[com.slothrop.player.lookup :refer [lookup-table]]
            #_[com.slothrop.cache.cache-config :refer [DEFAULT-CACHE-DIR]]))

;; a single day of game data for dev usage
(def results (send-req! {:game-date-gt "2022-05-01"
                         :game-date-lt "2022-05-02"
                         :hfTeam "BAL|"}))

;; sample tech.ml Dataset of above query results
#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(def table-data (d/->dataset results))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(def p (p/open {:launcher :vs-code}))

(add-tap #'p/submit)