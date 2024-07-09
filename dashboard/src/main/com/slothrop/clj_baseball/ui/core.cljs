(ns com.slothrop.clj-baseball.ui.core
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [dispatch reg-sub subscribe]]
   [taoensso.timbre :as log]
   ["antd" :refer [Button Col Divider Form Grid Image Input Row Spin Typography]]
   ["react"]))

(def Text (.-Text Typography))

(def Title (.-Title Typography))

(def Paragraph (.-Paragraph Typography))

(def form-item (.-Item Form))

(reg-sub
 :player/get-name
 (fn [db]
   (:player db)))

(reg-sub
 :player/show-name
 :<- [:player/get-name]
 (fn [{:keys [first-name last-name]}]
   (str first-name " " last-name)))

(defn PlayerProfile []
  (let [pname @(subscribe [:player/show-name])]
    [:div
     [:> Title]
     [:section
      [:> Image {:src "https://placehold.co/600x400/EEE/31343C"}]
      [:h3 pname]
      [:> Paragraph {} "Esse quam videri..."]]]))

(defn PlayerLookup []
  (r/with-let [form-state (r/atom [{:first-name {:name "first-name"
                                                 :value nil}
                                    :last-name {:name "last-name"
                                                :value nil}}])]
    [:div {:style {:display :flex
                   :flex-direction :column}}
     [:> Title {:style {:align-self :center}} "Player Lookup"]
     [:> Form
      {:name :player-lookup
       :labelCol {:span 8}
       :wrapperCol {:span 16}
       :initialValues {:remember true}
       :style {:maxWidth 600}
       :fields @form-state
       :onFieldsChange #(reset! form-state %)
       :onFinish (fn [values] (js/console.log "submitting") 
                   (dispatch [:player/search values]))
       :onFinishFailed #(js/console.log "Failed form")
       :autoComplete :off}
      [:> form-item {:label "Player first name"
                     :name "player-first"
                     :onChange #(js/console.log (.. % -target -value))
                     :rules [{:required true :message "Please enter a player's first name"}]}
       [:> Input]]
      [:> form-item {:label "Player last name"
                     :name "player-last"
                     :onChange #(js/console.log (.. % -target -value))
                     :rules [{:required true :message "Please enter a player's last name"}]}
       [:> Input]]
      [:> form-item {:wrapperCol {:offset 8 :span 16}}
       [:> Button {:type :primary
                   :htmlType :submit}
        "Lookup"]]]]))

(defn MainLayout []
  [:<>
   [:> Row
    [:> Col {:span 12}
     [PlayerLookup]]
    [:> Col {:span 12}
     [PlayerProfile]]]
   [:> Row
    [:> Col {:span 12}]
    [:> Col {:span 12}]]])