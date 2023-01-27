(ns com.slothrop.interop.py-config 
  (:require [libpython-clj2.python :refer [call-attr]]
            [libpython-clj2.require :refer [require-python]]
            [com.slothrop.statcast.batter :refer [send-req]]))

(require-python '[pandas :as pd]
                '[numpy :as np])

(defn create-dataframe [data] 
  (->> (send-req data)
       (apply merge-with (fn [& v] (into [] (flatten (conj [] v)))))
       (call-attr pd/DataFrame "from_dict")))