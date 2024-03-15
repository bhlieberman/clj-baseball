(ns com.slothrop.clj-baseball.dashboard.app.core
  (:require [com.fulcrologic.fulcro.algorithms.tx-processing.batched-processing :as btxn]
            [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.mutations :as m]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.fulcro.react.version18 :refer [with-react18]]
            [com.fulcrologic.rad.application :as rad-app]
            [com.fulcrologic.rad.rendering.semantic-ui.semantic-ui-controls :as sui]
            [com.fulcrologic.rad.report :as report]
            [com.fulcrologic.rad.routing :as routing]
            [com.fulcrologic.rad.routing.history :as history]
            [com.fulcrologic.rad.routing.html5-history :as hist5 :refer [new-html5-history]]
            [com.fulcrologic.rad.type-support.date-time :as datetime]
            [com.slothrop.clj-baseball.dashboard.app.ui :refer [Root]]))

(defn setup-RAD [app]
  (rad-app/install-ui-controls! app sui/all-controls)
  (report/install-formatter! app :boolean :affirmation (fn [_ value] (if value "yes" "no"))))

(defonce app (-> (rad-app/fulcro-rad-app {})
                 (with-react18)
                 (btxn/with-batched-reads)
                 #_(sync/with-synchronous-transactions #{:remote})))

(defn refresh []
  ;; hot code reload of installed controls
  (js/console.log "Reinstalling controls")
  (setup-RAD app)
  (comp/refresh-dynamic-queries! app)
  (app/force-root-render! app))

(defn init []
  (js/console.log "Starting App")
  ;; default time zone (should be changed at login for given user)
  (datetime/set-timezone! "America/Los_Angeles")
  ;; Avoid startup async timing issues by pre-initializing things before mount
  (app/set-root! app Root {:initialize-state? true})
  (dr/initialize! app)
  (setup-RAD app)
  (dr/change-route! app ["landing-page"])
  (history/install-route-history! app (new-html5-history {:app           app
                                                          :default-route {:route ["landing-page"]}}))
  (app/mount! app Root "app" {:initialize-state? false}))
