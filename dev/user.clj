(ns user
  (:require [com.slothrop.clj-baseball.backend.server :as server]
            [com.slothrop.clj-baseball.backend.duckdb :as db]
            [com.slothrop.clj-baseball.backend.app-config :as app]
            [com.stuartsierra.component :as component]
            [clojure.tools.namespace.repl :refer [set-refresh-dirs refresh]]))

(set-refresh-dirs "dev" "dashboard/src/main")

(defn start-system [opts]
  (let [{:keys [port]} opts]
    (component/system-map
     :server (server/web-server #'server/handler port)
     :app (app/->app {})
     :db (db/setup-db))))

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
  (stop)
  (reset))
