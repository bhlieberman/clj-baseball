(ns com.slothrop.clj-baseball.ui.client
  (:require [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.fulcrologic.fulcro.react.version18 :refer [with-react18]] 
            [com.slothrop.clj-baseball.ui.lookup :as lookup]
            [com.slothrop.clj-baseball.ui.core :as ui]))

(defonce app (with-react18 (app/fulcro-app)))

(defsc Root [this props]
  (dom/div
   (lookup/ui-player-profile {:player/name_ "Gunnar Henderson"})
   (ui/ant-divider)
   (lookup/ui-player-lookup)))

(defn ^:export init []
  (app/mount! app Root "root")
  (js/console.log "Loaded app..."))

(defn ^:export refresh []
  (app/mount! app Root "root")
  (comp/refresh-dynamic-queries! app)
  (js/console.log "Hot reloaded app..."))