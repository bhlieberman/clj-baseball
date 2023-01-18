(ns com.slothrop.sabermetrics.formulas
  "Contains the reference implementations of common baseball stats formulas
   according to the glossary provided by FanGraphs"
  (:require [clojure.spec.alpha :as s]))

(defn at-bats [{:keys [bb hbp pa sac interference]}]
  (- pa hbp bb sac interference))

(defn walk-rate [{:keys [bb pa]}] (/ bb pa))

(defn strikeout-rate [{:keys [k pa]}] (/ k pa))

(defn walk-strikeout-ratio [{:keys [bb k]}] (/ bb k))

(defn batting-avg [{:keys [h ab]}] (/ h ab))

(defn obp [{:keys [h bb hbp ab sf]}]
     (/ (+ h bb hbp) (+ ab bb hbp sf)))

(defn slugging [{:keys [singles doubles triples home-runs ab]}]
          (/ (+ singles doubles triples home-runs) ab))
