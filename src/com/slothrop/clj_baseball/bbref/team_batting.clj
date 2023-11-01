(ns com.slothrop.clj-baseball.bbref.team-batting
  (:require [com.slothrop.clj-baseball.bbref.datasource :refer [-get]]
            [clojure.string :as string]))

(defn team-batting
  ([team start-season]
   (team-batting team start-season start-season))
  ([team start-season end-season]
   (let [url (format "https://www.baseball-reference.com/teams/%s" team)
         seasons
         (for [season (range start-season (inc end-season))
               :let [season-url (format "%s/%s.shtml" url season)
                     response (-get season-url)
                     table (-> response
                               (.getElementsByTag "table")
                               (.select ".sortable")
                               first)
                     rows (.getElementsByTag table "tr")]]
           (for [row rows
                 :let [cols (.getElementsByTag row "td")
                       xf (comp 
                           (map (comp #(string/replace % #"\*" "")
                                      #(string/replace % #"#" "") 
                                      #(.text %)))
                           (remove (fn [s] 
                                     (or (string/includes? s "Totals")
                                         (string/includes? s "NL teams")
                                         (string/includes? s "AL Teams")))))
                       col-text (into [] xf cols)]]
             col-text))]
     seasons)))

(comment (team-batting "BAL" 2000))