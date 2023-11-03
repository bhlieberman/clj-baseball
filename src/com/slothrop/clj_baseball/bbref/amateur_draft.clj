(ns com.slothrop.clj-baseball.bbref.amateur-draft
  (:require [com.slothrop.clj-baseball.bbref.datasource :refer [-get]]
            [tablecloth.api.columns :as c]
            [tech.v3.dataset :as d])
  (:import [org.jsoup.nodes Document Element]
           [org.jsoup.select Elements]))

(defonce url "https://www.baseball-reference.com/draft/?year_ID=%s&draft_round=%s&draft_type=junreg&query_type=year_round&")

(defn get-draft-results [year draft-round]
  (let [-url (format url year draft-round)
        ^Document response (-get -url)
        ^Element table (.getElementById response "draft_stats")
        headings (into [] (comp (map #(.ownText ^Element %)) (take 25)) ^Elements (.getElementsByTag table "th"))
        rows (.. table (getElementsByTag "tr"))]
    (-> (->> (for [^Element row (rest rows)]
               (map #(.text ^Element %)
                    (concat (.getElementsByTag row "th")
                            (.getElementsByTag row "td"))))
             (map (partial zipmap headings))
             (d/->>dataset {:dataset-name "Draft Results"}))
        (d/remove-columns
         ["WAR" "G" "AB" "HR" "BA" "OPS"
          "G.1" "W" "L" "ERA" "WHIP" "SV"])
        (c/reorder-columns headings))))

(comment (get-draft-results 2023 3))