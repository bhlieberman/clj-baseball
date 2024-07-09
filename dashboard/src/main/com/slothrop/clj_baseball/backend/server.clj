(ns com.slothrop.clj-baseball.backend.server
  (:require
   [org.httpkit.server :as http]
   [clojure.java.io :as io]
   [com.slothrop.clj-baseball.api :as api]
   [com.slothrop.clj-baseball.backend.app-config :as config]
   [com.slothrop.clj-baseball.backend.duckdb :as db]
   [com.stuartsierra.component :as component]
   [muuntaja.middleware :as mw]
   [muuntaja.core :as m]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.params :refer [wrap-params]]
   [tablecloth.api.dataset :as td]
   [reitit.ring :as ring]
   [taoensso.timbre :as log]))

(defn middleware [app-component]
  (fn [handler]
    (-> handler
        (config/add-app-component app-component)
        mw/wrap-format
        wrap-keyword-params
        wrap-params)))

(def router
  (ring/router
   ["/"
    ["api" {:post (fn [{:keys [body]
                        :app/keys [component]}]
                    (let [{:keys [player-first player-last]} (m/decode "application/json" body)
                          {:keys [db]} component]
                      (log/info component)
                      {:body (m/encode "application/json"
                                       {:mlb-id (db/get-player-info db {:player-first player-first
                                                                        :player-last player-last})})
                       :status 200}))}]
    ["ping" {:get (fn [req] {:body (pr-str req)
                             :status 200})}]
    ["js/*" (ring/create-resource-handler {:root "public/js"})]
    ["" {:get (fn home [_] {:body (slurp (io/resource "public/index.html"))
                            "Content-Type" "text/html"
                            :status 200})}]]
   {:data {:muuntaja m/instance}}))

(defn handler [app]
  (ring/ring-handler
   router
   (ring/create-default-handler
    {:not-found {:status 404 :body "Not found"}})
   {:middleware [(middleware app)]}))

(defrecord Server [handler port app state]
  component/Lifecycle
  (start [comp]
    (if state
      comp
      (do (log/info "Starting server.")
          (assoc comp :server (http/run-server (handler app) {:port port})))))
  (stop [comp]
    (log/info "Stopping server.")
    (when-some [conn (:server comp)]
      (conn)
      (assoc comp :server nil))))

(defn web-server [handler port]
  (component/using (map->Server {:port port :handler handler})
                   [:app]))