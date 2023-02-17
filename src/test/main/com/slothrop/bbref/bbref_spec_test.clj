(ns bbref.bbref-spec-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer [deftest is run-tests]]
            [clojure.string :as string]))

(s/def ::player-profile
  (s/keys :req-un [::bbref-id ::name]
          :opt-un [::team ::league]))

(s/def ::name string?)

