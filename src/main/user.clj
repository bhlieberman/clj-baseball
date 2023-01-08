(ns main.user
  (:require [portal.api :as p]
            [clojure.string :as string]))

(def p (p/open {:launcher :vs-code}))
(add-tap #'p/submit)