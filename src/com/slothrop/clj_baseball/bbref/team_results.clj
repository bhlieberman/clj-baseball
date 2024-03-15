(ns com.slothrop.clj-baseball.bbref.team-results
  (:require [com.slothrop.clj-baseball.bbref.datasource :refer [-get]]
            [com.slothrop.clj-baseball.utils :refer [most-recent-season]]
            [clojure.pprint :as pprint]
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

(defmacro transform-cols []
  (let [cols (gensym "cols")]
    `(cond-> ~cols
       ~@(mapcat (fn [coll]
                   (let [num (first coll)
                         value (second coll)]
                     `[(string/blank? (.ownText ^Element (get ~cols ~num)))
                       (assoc! ~num (.text ^Element (get ~cols ~num) ~value))]))
                 [[1 "BAL"] [3 "Home"] [12 "None"]
                  [13 "None"] [14 "None"] [8 "9"]
                  [15 "Unknown"] [16 "Unknown"] [17 "Unknown"]]))))

(defn- get-table [^Document html team]
  (if-some [^Element table (.selectFirst html (Evaluator$Tag. "table"))]
    (let [headings (into [] (comp (map #(.text ^Element %)) (drop 1)) (.. table (selectFirst (Evaluator$Tag. "tr")) (getElementsByTag "th")))
          headings (assoc headings 3 "Home_Away")
          body (.. table (selectFirst (Evaluator$Tag. "tbody")))
          ^Elements rows (.getElementsByTag body "tr")
          data (for [^Element row (butlast rows)
                     :let [^Elements cols (transient (into [] (.getElementsByTag row "td")))]
                     :when (not (zero? (count cols)))
                     :let [cols (cond-> cols
                                  (string/blank? (.ownText ^Element (get cols 1))) (assoc! 1 (.text ^Element (get cols 1) team))
                                  (string/blank? (.ownText ^Element (get cols 3))) (assoc! 3 (.text ^Element (get cols 3) "Home"))
                                  (string/blank? (.ownText ^Element (get cols 12))) (assoc! 12 (.text ^Element (get cols 12) "None"))
                                  (string/blank? (.ownText ^Element (get cols 13))) (assoc! 13 (.text ^Element (get cols 13) "None"))
                                  (string/blank? (.ownText ^Element (get cols 14))) (assoc! 14 (.text ^Element (get cols 14) "None"))
                                  (string/blank? (.ownText ^Element (get cols 8))) (assoc! 8 (.text ^Element (get cols 8) "9"))
                                  (string/blank? (.ownText ^Element (get cols 16))) (assoc! 16 (.text ^Element (get cols 16) "Unknown"))
                                  (string/blank? (.ownText ^Element (get cols 15))) (assoc! 15 (.text ^Element (get cols 15) "Unknown"))
                                  (string/blank? (.ownText ^Element (get cols 17))) (assoc! 17 (.text ^Element (get cols 17) "Unknown"))
                                  true persistent!)]]
                 (map #(.ownText %) cols))]
      (d/remove-column (->> data
                            (into [] (map (partial zipmap headings)))
                            (d/->>dataset {:dataset-name "Team Results"})) ""))
    (throw (ex-info "No data for this team/season combo. Please verify that the team abbreviation is accurate and that the existed during the season." {}))))

(comment
  (-> (get-html 2023 "BAL")
      (get-table "BAL"))
  (println *1))