(ns com.slothrop.clj-baseball.dashboard.app.ui
  (:require
   #?@(:cljs [[com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown :refer [ui-dropdown]]
              [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-menu :refer [ui-dropdown-menu]]
              [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-item :refer [ui-dropdown-item]]])
   #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div label input]]
      :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div label input]]) 
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom.html-entities :as ent]
   [com.fulcrologic.fulcro.routing.dynamic-routing :refer [defrouter]]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [com.fulcrologic.rad.routing :as rroute]))

#_{:clj-kondo/ignore [:unresolved-symbol]}
(defsc Root [this {:keys []}]
  (dom/div "HELLO FROM FULCRO RAD"))