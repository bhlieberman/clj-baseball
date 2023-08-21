(ns com.slothrop.clj-baseball.statcast.utils
  (:require [com.slothrop.statcast.batter :refer [query-defaults]]
            [clojure.string :as string]))

(defn transform-vals [m]
  (reduce-kv (fn [m k v] (cond-> m
                           (or (set? v) (vector? v)) (assoc k (string/join "|" v))
                           (string? v) (assoc k v))) {} m))

(def test-query {:hfTeam ["BAL" "PIT" "CHC"]})

(defn transform-existing-query-vals [l r]
  (cond (vector? l) (conj l r)
        (and (string? l) (vector? r)) (conj r l)
        (nil? l) (identity r)
        (nil? r) (identity l)
        :else (throw (ex-info "cannot merge default and custom query maps." {:left-val l :right-val r}))))

(merge-with transform-existing-query-vals (assoc query-defaults :hfTeam "NYY") test-query)