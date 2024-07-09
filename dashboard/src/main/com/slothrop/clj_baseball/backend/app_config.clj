(ns com.slothrop.clj-baseball.backend.app-config
  (:require [com.stuartsierra.component :as component]))

(defrecord Application [config db state]
  component/Lifecycle
  (start [comp] 
    (assoc comp :state :running))
  (stop [comp]
    (assoc comp :state :stopped)))

(defn ->app [config]
  (component/using (map->Application {:config config})
                   [:db]))

(defn add-app-component [handler app]
  (fn [req]
    (handler (assoc req :app/component app))))