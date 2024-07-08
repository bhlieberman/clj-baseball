(ns com.slothrop.clj-baseball.backend.resolvers
  (:require
   [com.slothrop.clj-baseball.player.lookup :as lookup]
   [com.wsscode.pathom.core :as p]
   [com.wsscode.pathom.connect :as pc]
   [tablecloth.api.dataset :as td]))

(defn get-player-id [first-name last-name]
  (td/get-entry (lookup/search {:player-first first-name :player-last last-name}) "key_mlbam" 0))

(pc/defresolver player-id-resolver [env {:player/keys [first-name last-name]}]
  {::pc/output [:player/mlb-id]}
  {:player/mlb-id (get-player-id first-name last-name)})