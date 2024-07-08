(ns user
  (:require [com.slothrop.clj-baseball.backend.server :as server]
            [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [clojure.tools.namespace.repl :refer [set-refresh-dirs refresh]]))

(set-refresh-dirs "dev" "dashboard/src/main")

(defn start-system [opts]
  (let [{:keys [port]} opts]
    (component/system-map
     :server (server/map->Server {:port port}))))

(def system (start-system {:port 3000}))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system component/stop))

(defn reset []
  (stop)
  (refresh :after 'user/start))

(comment
  (start)
  (reset))