(ns com.slothrop.interop.dataframes 
  (:require [libpython-clj2.python :refer [call-attr]]
            [libpython-clj2.require :refer [require-python]]))

(require-python '[pandas :as pd]
                '[numpy :as np])

(defn ^:deprecated create-dataframe-from-dict 
  {:doc "Converts a sequence of maps into a single map with sequential values. 
         Passes the result to the DataFrame.from_dict class method.
         Prefer create-dataframe-from-records when using the primary API provided in the statcast.batter ns.
         This function will be removed in the 0.4.0 major release. Use the functionality provided by TMD and Tablecloth instead."}
  [data] 
  (->> data
       (apply merge-with 
              (fn [fst snd] (if (vector? fst) 
                              (conj fst snd) 
                              (vector fst snd))))
       (call-attr pd/DataFrame "from_dict")))

(defn ^:deprecated create-dataframe-from-records 
  {:doc "Passes a sequence of maps directly to the DataFrame.from_records class method.
         This function will be removed in the 0.4.0 major release. Use the functionality provided by TMD and Tablecloth instead."}
  [data]
  (call-attr pd/DataFrame "from_records" data))