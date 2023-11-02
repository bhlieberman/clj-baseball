(ns com.slothrop.clj-baseball.bbref.team-batting
  (:require [com.slothrop.clj-baseball.bbref.datasource :refer [-get]]
            [clojure.string :as string]
            [tablecloth.api.columns :as c]
            [tech.v3.dataset :as d]))

(defn team-batting
  ([team start-season]
   (team-batting team start-season start-season))
  ([team start-season end-season]
   (assert (some? start-season) "You must include a start season.")
   (let [url (format "https://www.baseball-reference.com/teams/%s" team)
         seasons
         (for [season (range start-season (inc end-season))
               :let [season-url (format "%s/%s.shtml" url season)
                     response (-get season-url)
                     table (-> response
                               (.getElementsByTag "table")
                               (.select ".sortable")
                               first)
                     rows (.getElementsByTag table "tr")
                     headers (subvec (into [] (map #(.text %) (.getElementsByTag table "th"))) 1 28)
                     w-year (assoc headers 2 "Year")]]
           (map (partial zipmap w-year)
                (for [row rows
                      :let [cols (into [] (.getElementsByTag row "td"))
                            xf (comp
                                (map (comp #(string/replace % #"\*" "")
                                           #(string/replace % #"#" "")
                                           #(.text %)))
                                (remove (fn [s]
                                          (or (string/includes? s "Totals")
                                              (string/includes? s "NL teams")
                                              (string/includes? s "AL Teams")))))
                            col-text (into [] xf cols)
                            w-season (when (< 1 (count col-text)) (assoc col-text 2 season))]]
                  w-season)))]
     (c/reorder-columns (->> seasons
                             (into [] (comp cat (remove empty?) (remove nil?)))
                             (d/->>dataset {:dataset-name "Team Batting"}))
                        ["Rk" "Pos" "Year" "Name"
                         "Age" "G" "PA" "AB" "R"
                         "H" "2B" "3B" "HR" "RBI"
                         "SB" "CS" "BB" "SO" "BA"
                         "OBP" "SLG" "OPS" "OPS+"
                         "TB" "GDP" "HBP" "SH" "SF" "IBB"]))))

(comment (team-batting "BAL" 2000))