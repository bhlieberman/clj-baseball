(ns com.slothrop.sabermetrics.formulas
  "Contains the reference implementations of common baseball stats formulas
   according to the glossary provided by FanGraphs"
  (:require [clojure.spec.alpha :as s]
            [com.slothrop.player.profile-spec :as pspec]))

(s/check-asserts true)

(s/def ::bb int?) ; base on balls - aka walks
(s/def ::hbp int?) ; hit by pitch
(s/def ::pa ::pspec/plate-appearances) ; plate appearances
(s/def ::sac int?) ; sacrifices hits or bunts
(s/def ::interference int?) ; batter reached on interference
(s/def ::at-bats-args (s/keys :req-un [::bb ::hbp ::pa
                                       ::sac ::interference]))
(defn at-bats [m]
  (let [conf (s/conform ::at-bats-args m)]
    (if (s/invalid? conf)
      (throw (ex-info "Missing a value needed to compute at bats."
                      (->> m 
                           (s/explain-data ::at-bats-args) 
                           :clojure.spec.alpha/problems
                           first))) 
      (let [{:keys [pa hbp bb sac interference]} conf]
        (- pa hbp bb sac interference)))))

(s/def ::walk-rate-args (s/keys :req-un [::bb ::pa]))
(defn walk-rate [{:keys [bb pa]}] 
  (try (/ bb pa)
       (catch ArithmeticException _
         (println "plate appearances must be non-zero."))
       (catch NullPointerException _
         (println "one of the required values was null"))))

(s/def ::k int?) ; strikeouts
(s/def ::strikeout-rate-args (s/keys :req-un [::k ::pa]))
(defn strikeout-rate [{:keys [k pa]}]
  (try (/ k pa)
       (catch ArithmeticException _
         (println "plate appearances must be non-zero."))
       (catch NullPointerException _
         (println "one of the required values was null"))))

(defn walk-strikeout-ratio [{:keys [bb k]}] (/ bb k))

(defn batting-avg [{:keys [h ab]}] (/ h ab))

(defn obp [{:keys [h bb hbp ab sf]}]
  (/ (+ h bb hbp) (+ ab bb hbp sf)))

(defn slugging [{:keys [singles doubles triples home-runs ab]}]
  (/ (+ singles doubles triples home-runs) ab))


(at-bats {:bb 1 :hbp 2 :sac 3 :interference 4})
(walk-rate {:bb 1})