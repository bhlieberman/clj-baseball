(ns html-parsing.statcast.batter
  (:require #_[ring.util.codec :refer [form-encode]]
   [clojure.java.io :as io]
            [charred.api :refer [read-csv]]
            [table.core :refer [table]]
            #_[clojure.core.async :refer [>!! <!! go chan alts!! alts! <! >!]]
            #_[clj-http.client :as client]
            [portal.api :as p])
  (:import [java.time LocalDate]
           [java.io File]
           [java.net.http HttpClient #_HttpRequest$BodyPublishers
            HttpRequest HttpResponse$BodyHandlers]
           [java.net URI]))

(def p (p/open {:launcher :vs-code}))
(add-tap #'p/submit)

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
               :game_date_gt "2022-04-28"
               :game_date_lt "2022-05-01"
               :sort-order "desc"
               :hfTeam "Orioles"))))

(comment
  (let [url-str "https://baseballsavant.mlb.com/statcast_search/csv"
        req (-> url-str (str "?hfPT=&hfAB=&hfGT=R%7C&hfPR=&hfZ=&hfStadium=&hfBBL=&hfNewZones=&hfPull=&hfC=&hfSea=2022%7C&hfSit=&player_type=batter&hfOuts=&hfOpponent=&pitcher_throws=&batter_stands=&hfSA=&game_date_gt=2022-08-03&game_date_lt=2022-08-03&hfMo=&hfTeam=&home_road=&hfRO=&position=&hfInfield=&hfOutfield=&hfInn=&hfBBT=&hfFlag=&metric_1=&group_by=name&min_pitches=0&min_results=0&min_pas=0&sort_col=pitches&player_event_sort=api_p_release_speed&sort_order=desc&type=details&all=true") (URI.) (HttpRequest/newBuilder) .build)
        client (HttpClient/newHttpClient)
        handler (HttpResponse$BodyHandlers/ofInputStream)
        out-str (-> (.send client req handler)
                    .body)]
    (with-open [wtr (io/writer (File. "search-results.txt"))
                rdr (io/reader out-str)]
      (.write wtr (apply str (line-seq rdr))))))

(def data (partition 92 (first (read-csv (File. "search-results.txt") :async? true :profile :immutable))))
