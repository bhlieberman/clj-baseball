(ns com.slothrop.clj-baseball.statcast.batter
  {:doc "Contains the implementation for the Statcast query endpoint."}
  (:require [clojure.string :as string]
            [clojure.edn :as edn]
            [clojure.spec.alpha :as s]
            [charred.api :refer [read-csv]]
            [tech.v3.dataset :as d]
            [clj-http.client :as client])
  (:import [java.net URLEncoder]))

(def query-defaults
  "The default map of query parameter values. Can be changed to modify the scope
   and size of your Statcast query."
  {:sort-col "pitches" :hfSit nil :hfPT nil,
   :hfOutfield nil :game-date-gt nil :min-pitches "0",
   :metric-1 nil :hfSA nil :hfInn nil,
   :hfTeam nil :game-date-lt nil :batter-stands nil,
   :sort-order "desc" :hfOuts nil :hfStadium nil,
   :player-event-sort "api_p_release_speed" :hfZ nil :type "details",
   :player-type "batter" :hfGT "R|" :hfC nil,
   :home-road nil :min-results "0" :hfRO nil,
   :hfOpponent nil :pitcher-throws nil :hfPull nil,
   :hfFlag nil :min-pas "0" :group-by "name",
   :hfBBT nil :position nil :hfSea nil ,
   :hfInfield nil :hfMo nil :hfPR nil,
   :hfBBL nil :hfAB nil :hfNewZones nil})

(defn- make-query-map
  {:doc "Modifies the query map stored in query-defaults with a user-specified
   map of search parameters."}
  [defaults params]
  #_{:pre [(s/valid? :com.slothrop.statcast.specs/query params)]
     :post [(s/valid? :com.slothrop.statcast.specs/query %)]}
  (merge defaults params))

(defn- make-query-string
  {:doc "Turns the map of query parameters into a query string compliant with
   Statcast's query endpoint. Does NOT perform URL encoding except
   where necessary. Arbitrary data that does not conform to the range of accepted
   values for a parameter will not modify the results."}
  [kvs]
  (letfn [(underscores [k] (-> k name (string/replace #"-" "_")))
          (make-qs [acc k v] (cond-> acc
                               (nil? v) (str (underscores k) "=&")
                               (some? v) (str (underscores k) "=" v "&")))]
    (-> make-qs (reduce-kv "" kvs) (str "all=true"))))

(defn- parse-double-vals [m]
  (let [ks (select-keys m [:release-pos-x :release-pos-y :release-pos-z :vx0 :vy0
                           :az :delta-run-exp :ay :pfx-x :pfx-z
                           :vz0 :ax :sz-bot :estimated-ba-using-speedangle :release-extension
                           :plate-x :plate-z :effective-speed :launch-speed
                           :sz-top :woba-value
                           :estimated-woba-using-speedangle :hc-y :hc-x])]
    (merge m (zipmap (keys ks) (map parse-double (vals ks))))))

(defn- parse-int-vals [m]
  (let [ks (select-keys m [:fielder-6 :release-spin-rate :fielder-7 :pitcher :delta-home-win-exp
                           :post-away-score :fielder-2 :zone :bat-score
                           :post-bat-score :fielder-1 :inning :at-bat-number
                           :launch-speed-angle :woba-denom :launch-angle :iso-value
                           :babip-value :release-speed :hit-distance-sc
                           :post-fld-score :home-score :fld-score :balls :away-score :strikes
                           :outs-when-up :spin-axis :fielder-9 :post-home-score
                           :pitch-number :fielder-5 :on-1b :on-2b :on-3b
                           :game-pk :batter :fielder-8 :fielder-3 :fielder-4])]
    (merge m (zipmap (keys ks) (map parse-long (vals ks))))))

(defn- encode-url-params [params]
  (reduce-kv (fn [m k ^String v]
               (assoc m k (some-> v (URLEncoder/encode "utf-8")))) {} params))

(defn send-req!
  {:doc "Sends the composed and spec'ed query to Statcast."}
  [params]
  #_{:post [(every? #(s/valid? :com.slothrop.statcast.results-spec/results %) %)]}
  (let [url "https://baseballsavant.mlb.com/statcast_search/csv?"
        qs (->> params (make-query-map query-defaults) encode-url-params make-query-string (str url))
        results (some-> qs
                        (client/get {:as :stream})
                        :body
                        read-csv)
        cols (map (comp keyword #(string/replace % #"_" "-")) (first results))]
    (->> (rest results)
         (map (comp parse-int-vals
                    parse-double-vals
                    (partial zipmap cols)))
         d/->dataset)))