(ns com.slothrop.interop.py-config 
  (:require [libpython-clj2.python :refer [call-attr]]
            [libpython-clj2.require :refer [require-python]]))

(require-python '[pandas :as pd]
                '[numpy :as np])

(defn create-dataframe [data] 
  (->> data
       (apply merge-with 
              (fn [fst snd] (if (vector? fst) 
                              (conj fst snd) 
                              (vector fst snd))))
       (call-attr pd/DataFrame "from_dict")))