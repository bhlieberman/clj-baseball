(ns com.slothrop.cache.cache-config
  (:import [java.nio.file Paths]))

(def ^:dynamic *cache-enabled?* false)

(defn enable []
  (set! *cache-enabled?* true))

(defn disable []
  (set! *cache-enabled?* false))

(def DEFAULT-CACHE-DIR 
  (Paths/get (System/getProperty "user.home") 
             (into-array [".clj-baseball" "cache"])))

