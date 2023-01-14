(ns com.slothrop.statcast.batter
  (:require
   [clojure.java.io :as io] 
   [clojure.string :as string]
   [clojure.edn :refer [read-string]]
   [charred.api :refer [read-csv]]
   [ring.util.response :refer [response]]
   [com.slothrop.http.client :refer [split-query send-split-reqs]]))

(def query-defaults
  (with-open [rdr (-> "public/query.edn" io/resource io/reader)]
    (read-string (slurp rdr))))

(defn make-query-map [defaults {:keys [date-start? date-end? team?] :as params}]
  (cond-> defaults
    date-start? (assoc "game-date-gt" (str "game-date-gt=" date-start?))
    date-end? (assoc "game-date-lt" (str "game-date-lt=" date-end?))
    team? (assoc "hfTeam" (str "hfTeam=" team?))))

(defn make-query-string [kvs]
  (letfn [(underscores [k] (-> k name (string/replace #"-" "_")))]
    (reduce-kv (fn [acc k v]
                 (cond-> acc
                   (nil? v) (str (underscores k) "=&")
                   (some? v) (str (underscores k) "=" v "&")))
               "" kvs)))

(defn send-batter-req [defaults date-start? date-end? team?]
  (let [base-url "https://baseballsavant.mlb.com/statcast_search/csv" 
        urls (map #(str base-url "?" % "&all=true") (make-query-string defaults))
        results (send-split-reqs urls)]
    (with-open [wtr (-> (io/file "search-results.txt") (io/writer :append true))]
      (doseq [r results]
        (->> r response :body read-csv (apply str) (.write wtr))))))
