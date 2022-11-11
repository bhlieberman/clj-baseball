(ns html-parsing.statcast.batter
  (:require [ring.util.codec :refer [form-encode]]
            [clojure.java.io :as io])
  (:import [org.jsoup Jsoup]
           [java.time LocalDate]
           [java.io File]
           [java.net.http HttpClient #_HttpRequest$BodyPublishers
            HttpRequest HttpResponse$BodyHandlers]
           [java.net URI]))

(def form-data [:pitch-type :pitch-result :batted-ball-location
                :count :player-type :pitcher-handedness :game-date-after
                :team :position :innning :flags :metric-range
                :group-by :min-pa :pa-result :gameday-zones :attack-zones
                :season :outs :batter-handedness :game-date-before
                :home-or-away :if-alignment :batted-ball-type
                :min-pitches :min-results])

(def query-keys
  "glossary of keywords:
   {:hfPT pitch-type
   :hfAB at-bats
   :GT game-type
   :hfPR pitch-result
   :hfStadium hosting-stadium
   :hfBBL batted-ball-location
   :hfNewZones ???
   :hfPull left-or-right
   :hfC count-balls-and-strikes
   :hfSea season
   :hfSit situation
   :hfOuts no-of-outs
   :game_date_gt game-date-until
   :game_date_lt game-date-from
   :hfMo month
   :hfTeam team-name
   :home_road home-or-away-game
   :hfRO runners-on-base
   :position field-position
   :hfInfield infield-alignment
   :hfOutfield outfield-alignment
   :hfInn innings
   :hfBBT batted-ball-type
   :hfFlags flags
   }"
  [:hfPT :hfAB :hfGT :hfPR :hfZ :hfStadium :hfBBL
   :hfNewZones :hfPull :hfC :hfSea :hfSit :player_type :hfOuts
   :hfOpponent :pitcher_throws :batter_stands :hfSA
   :game_date_gt :game_date_lt :hfMo :hfTeam :home_road :hfRO
   :position :hfInfield :hfOutfield :hfInn :hfBBT :hfFlag :metric_1
   :group_by :min_pitches :min_results :min_pas
   :sort_col :player_event_sort :sort_order])

(def ^:private batter-defaults
  "The default query map. Assumes nil values for all fields except :game-date-before,
   which receives a LocalDate object of today's date, and :player-type taking \"batter\""
  (let [times (count query-keys)]
    (-> query-keys
        (zipmap (repeat times nil))
        (assoc :player-type "batter"
               :game-date-before (LocalDate/now)))))

(let [times (count query-keys)
      filled-form (-> query-keys
                      (zipmap (repeat times nil))
                      (assoc :player-type "batter"
                             :game_date_gt (LocalDate/of 2022 04 01)
                             :game_date_lt (LocalDate/of 2022 07 01)
                             :sort-order "desc"
                             :hfTeam "Orioles")
                      seq
                      form-encode)
      url (URI. (str "https://baseballsavant.mlb.com/statcast_search?" filled-form))
      req (-> url
              (HttpRequest/newBuilder)
              .build)
      client (HttpClient/newHttpClient)
      handler (HttpResponse$BodyHandlers/ofInputStream)
      resp (.send client req handler)
      j (Jsoup/parse (.body resp) nil "https://baseballsavant.mlb.com/statcast_search")
      out-str (.text (.body j))]
  (with-open [wtr (io/writer (File. "search-results.txt"))]
    (.write wtr out-str)))