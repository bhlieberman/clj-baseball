(ns com.slothrop.clj-baseball.mutations
  (:require [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]))

(def lookup-ident [:component/id :com.slothrop.clj-baseball.ui.core/PlayerLookup])

(defmutation lookup-player [{:keys [player-first player-last]}]
  (action [{:keys [state]}]
          (js/console.log player-first player-last)#_
          (swap! state assoc-in [:player :player/name_] (str player-first " " player-last))))