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
(s/def ::k int?)

(s/def :outcome/type keyword?)
(defmulti at-bat :outcome/type)
;; the batter walks (spec)
(defmethod at-bat :outcome/walk [_]
  (s/keys :req [::bb ::pa ::k]))

;; the batter strikes out (spec)
(defmethod at-bat :outcome/k [_]
  (s/keys :req [::k ::bb ::pa]))

;; the batter hits a single (spec)
(defmethod at-bat :outcome/single [_]
  (s/keys :req [:pspec/avg :pspec/obp :pspec/slg]))

;; the batter hits a double (spec)
(defmethod at-bat :outcome/double [_]
  (s/keys :req [:pspec/avg :pspec/obp :pspec/slg]))

;; the batter hits a triple (spec)
(defmethod at-bat :outcome/triple [_]
  (s/keys :req [:pspec/avg :pspec/obp :pspec/slg]))

(s/def :outcome/outcome (s/multi-spec at-bat :outcome))

;; helper fn to compute batting average
(defn batting-avg [{::keys [h ab]}] (float (/ h ab)))

;; helper fn to compute strikeout rate
(defn k-rate [{::keys [k pa]}] (float (/ k pa)))

;; helper fn to compute walk rate
(defn bb-rate [{::keys [bb pa]}] (float (/ bb pa)))

;; helper fn to compute on-base percentage
(defn obp [{::keys [h bb hbp ab sf]}]
  (/ (+ h bb hbp) (+ ab bb hbp sf)))

;; helper fn to compute slugging percentage
(defn slugging [{::keys [singles doubles triples home-runs ab]}]
  (/ (+ singles doubles triples home-runs) ab))


;; dispatches the correct calculation fn based on outcome type
(defmulti at-bat-outcome (fn [m] (:outcome/type m)))

(defmethod at-bat-outcome :outcome/walk [v] {:walks (inc (::bb v)) 
                                             :walk-rate (bb-rate v)})

(defmethod at-bat-outcome :outcome/k [v] {:k (inc (::k v)) :k-rate (k-rate v)})

(defmethod at-bat-outcome :outcome/single [v]
  (let [singles (inc (::singles v)) avg (/ (::hits v) (::ab v))]
    {:singles singles :batting-average avg}))

;; dispatches the above multimethods when data passes spec
;; or else throws an exception
(defmulti dispatch-f class)

(defmethod dispatch-f clojure.lang.IPersistentMap [v]
  (at-bat-outcome v))

(defmethod dispatch-f clojure.lang.Keyword [_]
  (throw (ex-info "Did not pass spec :outcome/outcome" {})))
