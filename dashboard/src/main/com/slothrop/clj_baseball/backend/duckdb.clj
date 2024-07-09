(ns com.slothrop.clj-baseball.backend.duckdb
  (:require [com.slothrop.clj-baseball.player.lookup :as l]
            [com.stuartsierra.component :as component]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [taoensso.timbre :as log]
            [tablecloth.api :as t]))

(def db-spec {:dbtype "duckdb" :dbname "chadwick"})

(comment (def l (l/lookup-table)))

(defn load-chadwick-db [database]
  (let [lookup (t/rows (l/lookup-table))]
    (log/info "Inserting chadwick register into DuckDB")
    (sql/insert-multi! (database) :lookup [:name_first :name_last :key_mlbam :key_retro
                                         :key_bbref :key_fangraphs :mlb_played_first
                                         :mlb_played_last :player_name] lookup)))

(defrecord Database [db-spec db]
  component/Lifecycle
  (start [comp]
    (if db
      comp
      (let [datasource (assoc comp :db (jdbc/get-datasource db-spec))]
       (when-not
        (try (jdbc/execute! datasource
                             ["CREATE TABLE mlb_lookup (
                                    name_first varchar,
                                    name_last varchar,
                                    key_mlbam int,
                                    key_retro varchar,
                                    key_bbref varchar,
                                    key_fangraphs int,
                                    mlb_played_first int,
                                    mlb_played_last int,
                                    player_name varchar
                                  )"])
              (catch clojure.lang.ExceptionInfo e
                (log/error e)
                e))
         (load-chadwick-db datasource))
       datasource)))
  (stop [comp]
    (assoc comp :db nil))
  clojure.lang.IFn
  (invoke [_] db))

(defn setup-db []
  (map->Database {:db-spec db-spec}))

(defn get-player-info [database {:keys [player-first player-last]}]
  (jdbc/execute! (database) [(format "select key_mlbam from mlb_lookup where name_last = %s
                                         and name_first = %s" player-last player-first)]))

(comment
  (jdbc/execute! (jdbc/get-datasource db-spec) ["select 42"]))