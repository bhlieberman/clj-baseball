(ns com.slothrop.statcast.batter
  (:require
   [clojure.java.io :as io]
   [clojure.walk :refer [postwalk-replace walk]]
   [clojure.string :as string]
   [charred.api :refer [read-csv]]
   [ring.util.response :refer [response]]
   [http.client :refer [split-query send-split-reqs]])
  (:import [java.io File]))

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

(defn replace-default-vals
  "Updates the default query map/string with provided values."
  [kvs defaults]
  (->> defaults
       (postwalk-replace kvs)
       (walk (fn [d] (let [[f & rest] d
                           c (count rest)] (condp = f
                                           1 (if-not (.contains f "=")
                                             (str f "=")
                                             f)
                                           2 (string/join "=" rest))))#_(condp = (count %)
                1 (if-not (.contains (first %) "=") (str (first %) "=") (first %))
                2 (string/join "=" %)) #(string/join "&" %))))

(defn send-batter-req [defaults date-start? date-end? team?]
  (let [base-url "https://baseballsavant.mlb.com/statcast_search/csv"
        ds (split-query date-start? date-end?)
        new-vals {"game_date_gt" nil
                  "game_date_lt" nil
                  "hfTeam" nil}
        make-query-string (map (fn [[start end]]
                  (-> new-vals
                      (assoc "game_date_gt" (str "game_date_gt=" start)
                             "game_date_lt" (str "game_date_lt=" end)
                             "hfTeam" (str "hfTeam=" team?))
                      (replace-default-vals defaults))))
        urls (map #(str base-url "?" % "&all=true") (make-query-string ds))
        results (send-split-reqs urls)]
    (with-open [wtr (io/writer (File. "search-results.txt") :append true)]
      (doseq [r results]
        (->> r response :body read-csv (apply str) (.write wtr))))))