(ns com.slothrop.clj-baseball.bbref.team-pitching
  (:require [com.slothrop.clj-baseball.bbref.datasource :refer [-get]]
            [clojure.string :as string]
            [tech.v3.dataset :as d]
            [tablecloth.api.columns :as c]))

(defn team-pitching
  ([team start-season]
   (team-pitching team start-season start-season))
  ([team start-season end-season]
   (assert (some? start-season) "You must provide a starting season.")
   (let [url (format "https://www.baseball-reference.com/teams/%s" team)
         seasons (for [season (range start-season (inc end-season))
                       :let [season-url (format "%s/%s.shtml" url season)
                             response (-get season-url)
                             table (into [] (.. response
                                                (getElementsByTag "table")
                                                (select "#team_pitching")))
                             headings (subvec (into [] (map #(.text %))
                                                    (.getElementsByTag (first table) "th"))
                                              1 34)
                             w-year (assoc headings 2 "Year")
                             rows (.getElementsByTag (first table) "tr")]]
                   (map (partial zipmap w-year)
                        (for [row rows
                              :let [cols (.getElementsByTag row "td")
                                    xf (comp
                                        (map (comp
                                              #(string/replace % #"\*" "")
                                              #(string/replace % #"\#" "")
                                              #(.text %)))
                                        (remove (fn [s]
                                                  (or (string/includes? s "Totals")
                                                      (string/includes? s "AL teams")
                                                      (string/includes? s "NL teams")))))
                                    cols-text (into [] xf cols)
                                    w-season (when (< 1 (count cols-text)) (assoc cols-text 2 season))]]
                          w-season)))]
     (c/reorder-columns
      (->> seasons
           (into [] cat)
           (d/->>dataset {:dataset-name "Team Pitching"}))
      ["Pos" "Name" "Age" "W" "L"
       "W-L%" "ERA" "G" "GS" "GF"
       "CG" "SHO" "SV" "IP" "H"
       "R" "ER" "HR" "BB" "IBB" "SO"
       "HBP" "BK" "WP" "BF" "ERA+"
       "FIP" "WHIP" "H9" "HR9" "BB9"
       "SO9" "SO/W"]))))

(comment (team-pitching "BAL" 2023))