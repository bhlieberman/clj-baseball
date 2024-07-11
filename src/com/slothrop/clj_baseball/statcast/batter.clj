(ns com.slothrop.clj-baseball.statcast.batter
  {:doc "Contains the implementation for the Statcast query endpoint."}
  (:require [com.slothrop.clj-baseball.http-utils :refer [client]]
            [hato.client :as hc]
            [tech.v3.dataset :as d]))

(def query-defaults
  "The default map of query parameter values. Can be changed to modify the scope
   and size of your Statcast query."
  {:all true
   :sort_col "pitches" :hfSit nil :hfPT nil,
   :hfOutfield nil :game_date_gt nil :min_pitches "0",
   :metric_1 nil :hfSA nil :hfInn nil,
   :hfTeam nil :game_date_lt nil :batter_stands nil,
   :sort_order "desc" :hfOuts nil :hfStadium nil,
   :player_event_sort "api_p_release_speed" :hfZ nil,
   :player_type "batter" :hfGT "R|" :hfC nil,
   :home_road nil :min_results "0" :hfRO nil,
   :hfOpponent nil :pitcher_throws nil :hfPull nil,
   :hfFlag nil :min_pas "0" :group_by "name",
   :hfBBT nil :position nil :hfSea nil ,
   :hfInfield nil :hfMo nil :hfPR nil,
   :hfBBL nil :hfAB nil :hfNewZones nil
   :minors false})

(defn- make-query-map
  {:doc "Modifies the query map stored in query-defaults with a user-specified
   map of search parameters."}
  [defaults params]
  #_{:pre [(s/valid? :com.slothrop.statcast.specs/query params)]
     :post [(s/valid? :com.slothrop.statcast.specs/query %)]}
  (merge defaults params))

(defn send-req!
  {:doc "Sends the composed and spec'ed query to Statcast."}
  [params]
  #_{:post [(every? #(s/valid? :com.slothrop.statcast.results-spec/results %) %)]}
  (let [url "https://baseballsavant.mlb.com/statcast_search/csv"
        qs (make-query-map query-defaults params)]
    (some->
     (hc/get url {:as :stream
                  :http-client client
                  :query-params qs})
     :body
     (d/->dataset {:dataset-name "Statcast Results"
                   :file-type :csv
                   :column-blocklist ["xba"]}))))
