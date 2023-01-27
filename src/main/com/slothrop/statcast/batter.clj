(ns com.slothrop.statcast.batter
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.edn :refer [read-string]]
            [clojure.spec.alpha :as s]
            [ring.util.response :refer [response]]
            [charred.api :refer [read-csv]]
            [clj-http.client :as client]))

(def query-defaults
  "The default map of query parameter values. Can be changed to modify the scope
   and size of your Statcast query."
  (with-open [rdr (-> "public/query.edn" io/resource io/reader)]
    (read-string (slurp rdr))))

(defn make-query-map
  "Modifies the query map stored in query-defaults with a user-specified
   map of search parameters."
  [defaults params]
  {:pre [(s/valid? :com.slothrop.statcast.specs/query params)]
   :post [(s/valid? :com.slothrop.statcast.specs/query %)]}
  (merge defaults params))

(defn make-query-string
  "Turns the map of query parameters into a query string compliant with
   Statcast's query endpoint. Does NOT perform URL encoding except
   where necessary. Arbitrary data that does not conform to the range of accepted
   values for a parameter will not modify the results."
  [kvs]
  (letfn [(underscores [k] (-> k name (string/replace #"-" "_")))
          (make-qs [acc k v] (cond-> acc
                               (nil? v) (str (underscores k) "=&")
                               (some? v) (str (underscores k) "=" v "&")))]
    (-> make-qs (reduce-kv "" kvs) (str "all=true"))))

(defn send-req [params]
  (let [url "https://baseballsavant.mlb.com/statcast_search/csv?" 
        qs (->> params (make-query-map query-defaults) make-query-string (str url))
        results (-> qs client/get response :body :body read-csv)]
    (map (partial zipmap (first results)) (rest results))))

(time (send-req {:game-date-gt "2022-05-01" 
                 :game-date-lt "2022-05-30"
                 :hfTeam "BAL%7C"
                 :hfPTM "FF%7C"}))