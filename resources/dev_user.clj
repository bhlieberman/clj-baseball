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
            [tech.v3.dataset.column-filters :as cf]
            [tech.v3.dataset.join :as j]
            [tech.v3.dataset.zip :as z]
            [tech.v3.dataset.io.csv :as csv]
            [com.slothrop.statcast.batter :refer [send-req!]]
            [com.slothrop.statcast.results-spec :as-alias rspec]
            [com.slothrop.bbref.batting :refer [data rows]]
            [com.slothrop.player.lookup :refer [lookup-player-by-mlbid lookup-table-v2 table-csv]]
            [com.slothrop.cache.cache-config :refer [DEFAULT-CACHE-DIR]])
  (:import [java.util.zip ZipFile]
           [me.xdrop.fuzzywuzzy FuzzySearch]))

;; a single day of game data for dev usage
(def results (send-req! {:game-date-gt "2022-05-01"
                         :game-date-lt "2022-05-02"
                         :hfTeam "BAL|"}))

;; sample tech.ml Dataset of above query results
(def table-data (d/->dataset results))

(def lookup-table (lookup-table-v2))

(defn get-closest-names [player-last player-first player-table]
  (let [player-names (into [] (:player_name player-table))
        most-similar {:player_name
                      (map #(.getString %) (FuzzySearch/extractTop (str player-first player-last)
                                                                   player-names
                                                                   5))}]
    (j/pd-merge (d/->dataset most-similar) player-table {:on :player_name})))

(comment 
  #_(d/filter lookup-table (fn [m] (let [vs ((juxt #(get % "key_retro")
                                                   #(get % "key_bbref")
                                                   #(get % "key_fangraphs")
                                                   #(get % "mlb_played_first")
                                                   #(get % "mlb_played_last")) m)]
                                     (every? nil? vs))))
  (d/row-map lookup-table (fn [row] (keep (fn [v] (if v v nil)) row)))
  (map #(.getString %) (FuzzySearch/extractAll (str "Adley" "Ruschmann") (into [] (:player_name lookup-table)) 65))
  (get-closest-names "Ruschmann" "" lookup-table)
  )

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
  (type (d/row-at lookup-table 0))
  (-> DEFAULT-CACHE-DIR type javadoc)
  (javadoc org.apache.http.message.BasicHttpResponse)
  (javadoc java.net.URI)
  (doc jio/input-stream))

(comment
  (d/descriptive-stats table-data)

  (-> table-data
      (d/filter-column :events (fn [event] (= event "double")))
      :pitcher
      first
      lookup-player-by-mlbid))

(comment #_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
 (def p (p/open {:launcher :vs-code}))

         (add-tap #'p/submit))