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
            [tech.v3.dataset :as d]
            [tech.v3.dataset.zip :as z]
            [tech.v3.dataset.io.csv :as csv]
            [com.slothrop.statcast.batter :refer [send-req!]]
            [com.slothrop.statcast.results-spec :as-alias rspec]
            [com.slothrop.bbref.batting :refer [data rows]]
            [com.slothrop.player.lookup :refer [lookup-player-by-mlbid table-csv]])
  #_{:clj-kondo/ignore [:unused-import]}
  (:import [java.net URLEncoder URI]
           [java.nio.file Paths]
           [java.util.zip ZipFile ZipInputStream]))


(def results (send-req! {:game-date-gt "2022-05-01"
                         :game-date-lt "2022-05-02"
                         :hfTeam "BAL|"}))

(def table-data (d/->dataset results))



#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(def chadwick-tables
  (let [re (-> "register-master.zip"
               jio/resource
               .toURI
               jio/file
               ZipFile.)]
    (->> re
         .entries
         iterator-seq
         (drop 3)
         (map (fn [f] (-> re (.getInputStream f) csv/csv->dataset)))
         (into []))))

(comment
  (javadoc org.apache.http.message.BasicHttpResponse)
  (javadoc java.net.URI)
  (doc jio/input-stream))

(comment
  (d/descriptive-stats table-data)

  (-> table-data
      (d/filter-column :events (fn [event] (= event "double")))
      :pitcher
      first
      lookup-player-by-mlbid)
  (def p (p/open {:launcher :vs-code}))
  (add-tap #'p/submit))