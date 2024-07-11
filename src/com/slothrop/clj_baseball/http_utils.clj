(ns com.slothrop.clj-baseball.http-utils
  (:require [hato.client :as hc]))

(def client (hc/build-http-client {:connect-timeout 10000
                                   :cookie-policy :all
                                   :redirect-policy :always}))