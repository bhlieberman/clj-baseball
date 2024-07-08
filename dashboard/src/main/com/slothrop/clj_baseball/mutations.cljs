(ns com.slothrop.clj-baseball.mutations
  (:require [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]))

(defmutation lookup-player [{:keys [player-first player-last]}]
  (action [{:keys [state]}]
          (swap! state assoc-in [:player :player/name_] 
                 (str player-first " " player-last))))