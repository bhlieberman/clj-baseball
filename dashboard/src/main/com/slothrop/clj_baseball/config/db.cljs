(ns com.slothrop.clj-baseball.config.db
  (:require [re-frame.core :refer [reg-event-fx reg-event-db trim-v]]))

(reg-event-fx
 :app/initialize-db
 (fn [_ _]
   {:db {:player {:first-name ""
                  :last-name ""}}}))

(reg-event-db
 :player/update-name
 [trim-v]
 (fn [db [{:keys [player-first player-last]}]]
   (-> db
       (assoc-in [:player :first-name] player-first)
       (assoc-in [:player :last-name] player-last))))