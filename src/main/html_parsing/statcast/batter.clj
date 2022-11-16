(ns html-parsing.statcast.batter
  (:require
   [clojure.java.io :as io]
   [clojure.walk :refer [postwalk-replace walk]]
   [clojure.test :refer [deftest is run-test]]
   [clojure.string :as string]
   [charred.api :refer [read-csv]]
   [html-parsing.http.client :refer [split-query send-split-reqs]])
  (:import [java.io File]
           [java.net.http HttpClient
            HttpRequest HttpResponse$BodyHandlers]
           [java.net URI]
           [org.apache.commons.io.input BOMInputStream]))

(def query-defaults
  "The default sequence of query opts. Assumes nil values for all fields except :game-date-gt,
   which receives a LocalDate object of today's date, and those which Statcast defines without adjustment."
  '(["hfPT"] ["hfAB"] ["hfGT" "R%7C"] ["hfPR"] ["hfZ"] ["hfStadium"] ["hfBBL"]
             ["hfNewZones"] ["hfPull"] ["hfC"] ["hfSea" "2022%7C"] ["hfSit"]
             ["player_type" "batter"] ["hfOuts"] ["hfOpponent"] ["pitcher_throws"]
             ["batter_stands"] ["hfSA"] ["game_date_gt"] ["game_date_lt"]
             ["hfMo"] ["hfTeam"] ["home_road"] ["hfRO"] ["position"] ["hfInfield"]
             ["hfOutfield"] ["hfInn"] ["hfBBT"] ["hfFlag"] ["metric_1"] ["group_by" "name"]
             ["min_pitches" "0"] ["min_results" "0"] ["min_pas" "0"] ["sort_col" "pitches"]
             ["player_event_sort" "api_p_release_speed"] ["sort_order" "desc"] ["type" "details"]))

(defn replace-default-val
  "Updates the default query map/string with provided values."
  [kvs defaults]
  (->> defaults
       (postwalk-replace kvs)
       (walk #(condp = (count %)
                1 (if-not (.contains (first %) "=") (str (first %) "=") (first %))
                2 (string/join "=" %)) #(string/join "&" %))))

(defn send-batter-req [defaults date-start? date-end? team?]
  (let [base-url "https://baseballsavant.mlb.com/statcast_search/csv"
        ds (split-query date-start? date-end?)
        qs (map (fn [[start end]]
                  (replace-default-val {"game_date_gt" (str "game_date_gt=" start)
                                        "game_date_lt" (str "game_date_lt=" end)
                                        "hfTeam" (str "hfTeam=" team?)} defaults)) ds)
        req (-> base-url
                (str "?" qstr "&all=true")
                (URI.)
                (HttpRequest/newBuilder)
                .build)
        handler (HttpResponse$BodyHandlers/ofInputStream)
        out-str (-> (HttpClient/newHttpClient)
                    (.send req handler)
                    .body
                    (BOMInputStream.))]
    (with-open [wtr (io/writer (File. "search-results.txt") :append true)
                rdr (io/reader out-str)]
      (.write wtr (apply str (read-csv rdr))))))

(deftest test-update-query-map
  (let [curr (count (replace-default-val {} query-defaults))
        new-vals (count (replace-default-val {"hfTeam" "hfTeam=BAL%7C"} query-defaults))]
    (is (= new-vals (+ 7 curr)))))

(deftest test-cache-file-write
  (let [#_(send-batter-req query-defaults "2022-05-01" "2022-05-02" "BAL%7C")
        buf (byte-array 100)]
    (is (true? (.exists (File. "search-results.txt"))))
    (with-open [is (io/input-stream (File. "search-results.txt"))]
      (.skip is 1)
      (.read is buf 0 100))
    (is (= (first buf) 34))))

(run-test test-cache-file-write)