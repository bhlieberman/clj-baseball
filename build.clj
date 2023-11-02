(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib 'slothrop/clj-baseball)
(def version "0.4.0")
(def class-dir "target/classes/")
(def basis (b/create-basis {}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn clean [_]
  (b/delete {:path "target"}))

(defn jar [_]
  (b/write-pom {:class-dir class-dir
                :src-pom "/home/bhlieberman/dev/clj/clj-baseball/pom.xml"
                :lib lib
                :version version
                :basis basis
                :src-dirs ["src"]})
  (b/copy-dir {:src-dirs ["src"] 
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))