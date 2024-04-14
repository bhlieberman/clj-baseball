(ns com.slothrop.clj-baseball.api
  "Public API for clj-baseball."
  #_{:clj-kondo/ignore [:unused-namespace :unused-referred-var]}
  (:require [com.slothrop.clj-baseball.statcast.batter :refer [send-req!]]
            [com.slothrop.clj-baseball.player.lookup :refer [search]]
            [com.slothrop.clj-baseball.bbref.league-batting :as lb]
            [com.slothrop.clj-baseball.bbref.team-batting :as b]
            [com.slothrop.clj-baseball.bbref.team-pitching :as p]
            [com.slothrop.clj-baseball.bbref.team-fielding :as f]))

(defn statcast
  "Send a query to the Statcast endpoint. Useful keys are:
   `:hfTeam`, `:game-date-lt`, `:game-date-gt`."
  [params]
  (send-req! params))

(defn mlb-player-id-lookup
  "Lookup the MLBID of a player (active or otherwise) in the Chadwick Bureau register."
  [params]
  (search params))

(defn league-batting
  "Return the league batting stats from Baseball Reference between `start-date` and `end-date`."
  [start-date end-date]
  (lb/batting-stats-table start-date end-date))

(defn team-batting
  "Return the given `team`'s batting stats from Baseball Reference between `start-date` and `end-date`."
  [team start-date end-date]
  (if (some? end-date)
    (b/team-batting team start-date end-date)
    (b/team-batting team start-date)))

(defn team-pitching
  "Return the given `team`'s pitching stats from Baseball Reference between `start-date` and `end-date`."
  [team start-date end-date]
  (if (some? end-date)
    (p/team-pitching team start-date end-date)
    (p/team-pitching team start-date)))

(defn team-fielding
  "Return the given `team`'s fielding stats from Baseball Reference between `start-date` and `end-date`."
  [team start-date end-date]
  (if (some? end-date)
    (f/team-fielding team start-date end-date)
    (f/team-fielding team start-date)))

(defn to-tmd-dataset 
  "Turns a Clojure data structure into a TMD dataset. Provided as a compatibility layer."
  [s])