(ns com.slothrop.clj-baseball.ui.client
  (:require [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.fulcrologic.fulcro.react.version18 :refer [with-react18]] 
            [com.slothrop.clj-baseball.ui.lookup :as lookup]
            [com.slothrop.clj-baseball.ui.core :as ui]))

(defonce app (with-react18 (app/fulcro-app)))

(defsc Root [this {:keys [player]}]
  {:query [{:player (comp/get-query lookup/PlayerProfile)} 
           {:lookup (comp/get-query ui/PlayerLookup)}]
   :initial-state (fn [_] {:player (comp/get-initial-state lookup/PlayerProfile)
                           :lookup (comp/get-initial-state ui/PlayerLookup)})}
  (dom/div
   (lookup/ui-player-profile player)
   (ui/ant-divider)
   (ui/ui-player-lookup {:player/first-name ""
                         :player/last-name ""
                         :ui/loading? false})))

(defn ^:export init []
  (app/mount! app Root "root")
  (js/console.log "Loaded app..."))

(defn ^:export refresh []
  (app/mount! app Root "root")
  (comp/refresh-dynamic-queries! app)
  (js/console.log "Hot reloaded app..."))