(ns com.slothrop.statcast.batter
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.edn :refer [read-string]]
            [clojure.spec.alpha :as s]
            [charred.api :refer [read-csv]]
            [ring.util.response :refer [response]]
            [clj-http.client :as client]
            [com.slothrop.http.client :refer [split-query send-split-reqs]]))

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
  (letfn [(underscores [k] (-> k name (string/replace #"-" "_")))]
    (reduce-kv (fn [acc k v]
                 (cond-> acc
                   (nil? v) (str (underscores k) "=&")
                   (some? v) (str (underscores k) "=" v "&")))
               "" kvs)))

(defn send-batter-req [{:keys [game-date-gt game-date-lt] :as params}] 
  (let [split-queries (map (fn [[b e]]
                             (assoc {}
                                    :game-date-lt (str b)
                                    :game-date-gt (str e)
                                    :team "BAL%7C"))
                           (split-query game-date-gt game-date-lt))
        base-url "https://baseballsavant.mlb.com/statcast_search/csv?"
        urls (map (fn [params] (str base-url (->> params
                                                  (make-query-map query-defaults)
                                                  make-query-string) "all=true")) split-queries)]
    (client/with-async-connection-pool {}
      (for [url urls]
        (-> url client/get response :body tap>)))))