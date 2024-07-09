(ns com.slothrop.clj-baseball.config.server-events
  (:require [re-frame.core :refer [reg-event-db reg-event-fx]]
            [superstructor.re-frame.fetch-fx]))

(reg-event-fx
 :player/search
 (fn [_ [_ params]]
   (let [fd (js/FormData.)]
     (doseq [[k v] (js/Object.entries params)]
       (.append fd k v))
     {:fetch {:method                 :post
              :url                    "http://localhost:3000/api"
              :mode                   :same-origin
              :body                   (js/JSON.stringify params)
              :timeout                5000
              :response-content-types {#"application/.*json" :json}
              :on-success             [:good-fetch-result]
              :on-failure             [:bad-fetch-result]}})))

(reg-event-db
 :good-fetch-result
 (fn [db [resp]]
   (assoc db :http/result resp)))