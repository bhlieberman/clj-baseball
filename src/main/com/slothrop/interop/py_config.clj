(ns main.com.slothrop.interop.py-config 
  #_{:clj-kondo/ignore [:unused-referred-var]}
  (:require [libpython-clj2.python
             :refer [as-python as-jvm
                     ->python ->jvm
                     get-attr call-attr call-attr-kw
                     get-item initialize!
                     run-simple-string
                     add-module module-dict
                     import-module
                     python-type
                     dir] :as py]
            [libpython-clj2.require :refer [require-python]]
            [com.slothrop.statcast.batter :refer [send-req]]))

(require-python '[pandas :as pd]
                '[numpy :as np])

(defn create-dataframe [data] 
  (->> (send-req data)
       (apply merge-with (fn [& v] (into [] (flatten (conj [] v)))))
       (call-attr pd/DataFrame "from_dict")))