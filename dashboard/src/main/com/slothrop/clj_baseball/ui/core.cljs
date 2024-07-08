(ns com.slothrop.clj-baseball.ui.core
  (:require [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.slothrop.clj-baseball.mutations :as api]
            ["antd" :refer [Button Divider Form Input Spin Typography]]))

(def ant-button (interop/react-factory Button))

(def ant-form (interop/react-factory Form))

(def ant-form-item (interop/react-factory (.-Item Form)))

(def ant-input (interop/react-input-factory Input))

(def ^:private Text (.-Text Typography))

(def ^:private Title (.-Title Typography))

(def ^:private Paragraph (.-Paragraph Typography))

(def ant-divider (interop/react-factory Divider))

(def ant-title (interop/react-factory Title))

(def ant-text (interop/react-factory Text))

(def ant-spin (interop/react-factory Spin))

(def ant-paragraph (interop/react-factory Paragraph))

(defsc PlayerLookup [this {:player/keys [first-name last-name]
                           :ui/keys [loading?]}]
  {:query [:player/first-name :player/last-name :ui/loading?]}
  (let [lookup-player-by-name (fn []
                                (comp/transact! this
                                               `[(api/lookup-player {:player-first ~first-name
                                                                     :player-last ~last-name})]))]
    (dom/div
     (if loading?
       (ant-spin {:fullscreen true})
       (ant-form {:name :player-lookup
                  :labelCol {:span 8}
                  :wrapperCol {:span 16}
                  :initialValues {:remember true}
                  :style {:maxWidth 600}
                  :onFinish lookup-player-by-name
                  :onFinishFailed #(js/console.log "Failed form")
                  :autoComplete :off}
                 (ant-form-item {:label "Player first name"
                                 :name "player-first"
                                 :rules [{:required true :message "Please enter a player's first name"}]}
                                (ant-input))
                 (ant-form-item {:label "Player last name"
                                 :name "player-last"
                                 :rules [{:required true :message "Please enter a player's last name"}]}
                                (ant-input))
                 (ant-form-item {:wrapperCol {:offset 8 :span 16}}
                                (ant-button {:type :primary
                                             :htmlType :submit}
                                            "Lookup")))))))

(def ui-player-lookup (comp/factory PlayerLookup))