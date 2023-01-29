(ns main.com.slothrop.utils.csv
  (:require [clojure.java.io :as io]
   [charred.api :refer [write-csv read-csv]]))

(defn write-to-csv! [path data]
  (with-open [wtr (io/writer path)]
    (write-csv wtr data)))