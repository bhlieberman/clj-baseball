(ns com.slothrop.clj-baseball.api
  "Public API for clj-baseball."
  (:require [com.slothrop.clj-baseball.statcast.batter :refer [send-req!]]
            [com.slothrop.clj-baseball.player.lookup :refer [search]]))

(defn statcast [params]
  (send-req! params))

(defn mlb-player-id-lookup [params]
  (search params))