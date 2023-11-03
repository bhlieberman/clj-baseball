(ns com.slothrop.clj-baseball.bbref.team-results
  (:require [com.slothrop.clj-baseball.bbref.datasource :refer [-get]]
            [com.slothrop.clj-baseball.utils :refer [most-recent-season]]
            [clojure.string :as string]
            [tech.v3.dataset :as d])
  (:import [org.jsoup.nodes Document Document$OutputSettings
            Document$OutputSettings$Syntax Element]
           [org.jsoup.select Elements Evaluator$Tag]))

(set! *warn-on-reflection* true)

(defn- get-html [season team]
  (let [season (or season (most-recent-season))
        url (format "http://www.baseball-reference.com/teams/%s/%s-schedule-scores.shtml" team season)]
    (-> url
        -get
        (as-> ^Document doc
              (.outputSettings doc
                               (.syntax (Document$OutputSettings.)
                                        Document$OutputSettings$Syntax/xml))))))

(defn- get-table [^Document html team]
  (if-some [^Element table (.selectFirst html (Evaluator$Tag. "table"))]
    (let [headings (into [] (comp (map #(.text ^Element %)) (drop 1)) (.. table (selectFirst (Evaluator$Tag. "tr")) (getElementsByTag "th")))
          headings (assoc headings 3 "Home_Away")
          body (.. table (selectFirst (Evaluator$Tag. "tbody")))
          ^Elements rows (.getElementsByTag body "tr")
          data (for [^Element row (butlast rows)
                     :let [^Elements cols (into [] (.getElementsByTag row "td"))]
                     :when (not (zero? (count cols)))
                     :let [cols (cond-> cols
                                  (string/blank? (.ownText ^Element (get cols 1))) (update 1 #(.text ^Element %1 %2) team)
                                  (string/blank? (.ownText ^Element (get cols 3))) (update 3 #(.text ^Element %1 %2) "Home")
                                  (string/blank? (.ownText ^Element (get cols 12))) (update 12 #(.text ^Element %1 %2) "None")
                                  (string/blank? (.ownText ^Element (get cols 13))) (update 13 #(.text ^Element %1 %2) "None")
                                  (string/blank? (.ownText ^Element (get cols 14))) (update 14 #(.text ^Element %1 %2) "None")
                                  (string/blank? (.ownText ^Element (get cols 8))) (update 8 #(.text ^Element %1 %2) "9")
                                  (string/blank? (.ownText ^Element (get cols 16))) (update 16 #(.text ^Element %1 %2) "Unknown")
                                  (string/blank? (.ownText ^Element (get cols 15))) (update 15 #(.text ^Element %1 %2) "Unknown")
                                  (string/blank? (.ownText ^Element (get cols 17))) (update 17 #(.text ^Element %1 %2) "Unknown"))]]
                 (map #(.ownText %) cols))]
      (d/remove-column (->> data
                            (into [] (map (partial zipmap headings)))
                            (d/->>dataset {:dataset-name "Team Results"})) ""))
    (throw (ex-info "No data for this team/season combo. Please verify that the team abbreviation is accurate and that the existed during the season." {}))))

(comment
  (-> (get-html 2023 "BAL")
      (get-table "BAL"))
  (println *1))