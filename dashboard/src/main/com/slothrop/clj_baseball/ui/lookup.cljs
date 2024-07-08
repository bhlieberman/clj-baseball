(ns com.slothrop.clj-baseball.ui.lookup
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.slothrop.clj-baseball.ui.core :as ui]))

(defsc PlayerLookup [this {:player/keys [first-name last-name]}]
  (dom/div
   (ui/player-lookup-form)))

(defsc PlayerProfile [this {:player/keys [name_ mlb-id]}]
  {:query [:player/name_ :player/mlb-id]}
  (dom/div
   (ui/ant-title {} "Player profile")
   (dom/section
    (dom/image {:src "https://placehold.co/600x400/EEE/31343C"})
    (dom/h3 name_)
    (dom/h6 mlb-id)
    (ui/ant-paragraph {} "Esse quam videri..."))))

(def ui-player-profile (comp/factory PlayerProfile))

(def ui-player-lookup (comp/factory PlayerLookup))