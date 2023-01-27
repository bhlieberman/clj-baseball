(ns resources.user
  (:require [portal.api :as p]
            [clojure.repl :refer [source doc]]
            [clojure.java.javadoc :refer [javadoc]]))

(def p (p/open {:launcher :vs-code}))
(add-tap #'p/submit)

(javadoc System)