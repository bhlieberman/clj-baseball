(ns com.slothrop.clj-baseball.statcast.batter
  {:doc "Contains the implementation for the Statcast query endpoint."}
  (:require [clojure.string :as string]
            [clojure.edn :as edn]
            [clojure.spec.alpha :as s]
            [charred.api :refer [read-csv]]
            [hato.client :as hc]
            [tech.v3.dataset :as d]))

(def query-defaults
  "The default map of query parameter values. Can be changed to modify the scope
   and size of your Statcast query."
  {:all true
   :sort-col "pitches" :hfSit nil :hfPT nil,
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

(defn make-query-map
  {:doc "Modifies the query map stored in query-defaults with a user-specified
   map of search parameters."}
  [defaults params]
  #_{:pre [(s/valid? :com.slothrop.statcast.specs/query params)]
     :post [(s/valid? :com.slothrop.statcast.specs/query %)]}
  (merge defaults params))

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

(defn send-req!
  {:doc "Sends the composed and spec'ed query to Statcast."}
  [params]
  #_{:post [(every? #(s/valid? :com.slothrop.statcast.results-spec/results %) %)]}
  (let [url "https://baseballsavant.mlb.com/statcast_search/csv"
        qs (make-query-map query-defaults params)
        results (some-> 
                 (hc/get url {:as :stream :query-params qs})
                 :body ;; I think the endpoint is down right now, check later
                 read-csv)
        cols (map (comp keyword #(string/replace % #"_" "-")) (first results))]
    (->> (rest results)
         (map (comp parse-int-vals
                    parse-double-vals
                    (partial zipmap cols)))
         d/->dataset)))