(ns com.slothrop.clj-baseball.backend.server
  (:require
   [com.slothrop.clj-baseball.backend.parser :refer [pathom-parser]]
   [org.httpkit.server :as http]
   [com.fulcrologic.fulcro.server.api-middleware :as server]
   [com.stuartsierra.component :as component]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.resource :refer [wrap-resource]]
   [taoensso.timbre :as log]))

(def ^:private not-found-handler
  (fn [req]
    {:status  404
     :headers {"Content-Type" "text/plain"}
     :body    "Not Found"}))

(def middleware
  (-> not-found-handler
      (server/wrap-api {:uri    "/api"
                        :parser pathom-parser})
      (server/wrap-transit-params)
      (server/wrap-transit-response)
      (wrap-resource "public")
      wrap-content-type))

(defonce stop-fn (atom nil))

(defrecord Server [port]
  component/Lifecycle
  (start [comp]
    (log/info "Starting server.")
    (assoc comp :server (http/run-server middleware {:port port})))
  (stop [comp]
    (log/info "Stopping server.")
    (when-some [conn (:server comp)]
      (conn)
      (assoc comp :server nil))))