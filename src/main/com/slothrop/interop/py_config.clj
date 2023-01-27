(ns main.com.slothrop.interop.py-config
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
            [libpython-clj2.require :refer [require-python]]))
  
(require-python '[pandas :no-arglists :as pd]
                   '[numpy :as np])

(initialize!)
