(ns com.slothrop.clj-baseball.ui.client
  (:require
   [re-frame.core :as rf]
   [reagent.dom.client :as rdc]
   [com.slothrop.clj-baseball.config.db]
   [com.slothrop.clj-baseball.config.server-events]
   [com.slothrop.clj-baseball.ui.core :as ui]))

(defonce root (rdc/create-root (.getElementById js/document "root")))

(defn render []
  (rdc/render root [ui/MainLayout]))

(defn ^:export init []
  (rf/dispatch-sync [:app/initialize-db])
  (render)
  (js/console.log "Loaded app..."))

(defn ^:export refresh []
  (rf/clear-subscription-cache!)
  (render)
  (js/console.log "Hot reloaded app..."))