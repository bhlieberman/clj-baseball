(ns com.slothrop.clj-baseball.bbref.team-fielding
  (:require [com.slothrop.clj-baseball.bbref.datasource :refer [-get]]
            [clojure.string :as string]
            [tablecloth.api.columns :as c]
            [tech.v3.dataset :as d])
  (:import [java.util List]
           [org.jsoup.nodes Element]
           [org.jsoup.select Elements Evaluator$Tag]))

(defn team-fielding
  ([team start-season]
   (team-fielding team start-season start-season))
  ([team start-season end-season]
   (assert (some? start-season) "You must provide a start season.")
   (let [url (format "https://www.baseball-reference.com/teams/%s" team)
         seasons (for [season (range start-season (inc end-season))
                       :let [season-url (format "%s/%s-fielding.shtml" url season)
                             response (-get season-url)
                             fielding-div (.getElementById response "all_standard_fielding")
                             el (Element. "div")
                             comments (first (map #(.getData %)
                                                  (-> fielding-div
                                                      List/of
                                                      (Elements.)
                                                      (.comments))))
                             _ (.html el comments)
                             table (.selectFirst el (Evaluator$Tag. "table"))
                             thead (.selectFirst table (Evaluator$Tag. "thead"))
                             headings (into [] (map #(.text %) (.getElementsByTag thead "th")))
                             w-year (assoc headings 2 "Year")
                             rows (-> table (.selectFirst (Evaluator$Tag. "tbody")) (.getElementsByTag "tr"))]]
                   (map (partial zipmap w-year)
                        (for [row rows
                              :let [cols (.getElementsByTag row "td")
                                    xf (comp
                                        (map (comp
                                              #(string/replace % #"\*" "")
                                              #(string/replace % #"\#" "")
                                              #(.text %)))
                                        (remove #(string/includes? % "Team Runs")))
                                    cols-text (into [] xf cols)
                                    w-season (when (< 1 (count cols-text)) (assoc cols-text 2 season))]]
                          w-season)))]
     (c/reorder-columns
      (->> seasons 
           (into [] cat)
           (d/->>dataset {:dataset-name "Team Fielding"}))
      ["Name" "Age" "G" "GS" "CG"
       "Inn" "Ch" "PO" "A" "E"
       "DP" "Fld%" "Rtot" "Rtot/yr"
       "RF/9" "RF/G" "PB" "WP" "SB"
       "CS" "CS%" "lgCS%" "PO" "Pos Summary"]))))

(comment (team-fielding "BAL" 2000))