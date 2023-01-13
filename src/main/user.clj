(ns main.user
  (:require [portal.api :as p]
            [clojure.string :as string]
            [clojure.java.io :as jio])
  (:import [java.util Date]
           [java.time Instant]))

(def p (p/open {:launcher :vs-code}))
(add-tap #'p/submit)

(with-open [rdr (jio/reader "/home/slothrop/Documents/clojure/statcast-keys.txt")]
  (let [txt (slurp rdr)]
    (->> (string/split txt #"\n")
         (map (fn [s]
                (-> s
                    (string/lower-case)
                    (string/replace #" " "-")
                    (string/replace #"/" "-")
                    keyword)))
         tap>)))

(Date/from (Instant/parse "2008-04-01T00:00:00Z"))