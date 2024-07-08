(ns com.slothrop.clj-baseball.ui.lookup
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.slothrop.clj-baseball.ui.core :as ui]))

(defsc PlayerProfile [_ {:player/keys [name_ mlb-id]}]
  {:query [:player/name_ :player/mlb-id]
   :initial-state {:player/name_ "" :player/mlb-id nil}
   :ident :player/mlb-id}
  (dom/div
   (ui/ant-title {} "Player profile")
   (dom/section
    (dom/image {:src "https://placehold.co/600x400/EEE/31343C"})
    (dom/h3 name_)
    (dom/h6 mlb-id)
    (ui/ant-paragraph {} "Esse quam videri..."))))

(def ui-player-profile (comp/factory PlayerProfile))
