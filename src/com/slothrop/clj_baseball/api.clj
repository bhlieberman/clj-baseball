(ns com.slothrop.clj-baseball.api
  "Public API for clj-baseball."
  #_{:clj-kondo/ignore [:unused-namespace :unused-referred-var]}
  (:require [com.slothrop.clj-baseball.statcast.batter :refer [send-req!]]
            [com.slothrop.clj-baseball.player.lookup :refer [search]]
            [com.slothrop.clj-baseball.bbref.league-batting :refer [batting-stats-table]]
            [com.slothrop.clj-baseball.bbref.team-batting :refer [team-batting]]
            [com.slothrop.clj-baseball.bbref.team-pitching :refer [team-pitching]]
            [com.slothrop.clj-baseball.bbref.team-fielding :refer [team-fielding]]))

(defn statcast [params]
  (send-req! params))

(defn mlb-player-id-lookup [params]
  (search params))