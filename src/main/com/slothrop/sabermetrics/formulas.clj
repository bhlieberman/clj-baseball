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

(defn batting-avg [{::keys [h ab]}] (/ h ab))
(defn k-rate [{::keys [k pa]}] (float (/ k pa)))
(defn bb-rate [{::keys [bb pa]}] (float (/ bb pa)))

(defn obp [{::keys [h bb hbp ab sf]}]
  (/ (+ h bb hbp) (+ ab bb hbp sf)))

(defn slugging [{::keys [singles doubles triples home-runs ab]}]
  (/ (+ singles doubles triples home-runs) ab))

(s/def :outcome/type keyword?)
(defmulti at-bat :outcome/type)
(defmethod at-bat :outcome/walk [_]
  (s/keys :req [::bb ::pa ::k]))
(defmethod at-bat :outcome/k [_]
  (s/keys :req [::k ::bb ::pa]))
(defmethod at-bat :outcome/single [_]
  (s/keys :req [:pspec/avg :pspec/obp :pspec/slg]))
(defmethod at-bat :outcome/double [_]
  (s/keys :req [:pspec/avg :pspec/obp :pspec/slg]))
(defmethod at-bat :outcome/triple [_]
  (s/keys :req [:pspec/avg :pspec/obp :pspec/slg]))


(s/def :outcome/outcome (s/multi-spec at-bat :outcome))

(defmulti at-bat-outcome (fn [m] (:outcome/type m)))
(defmethod at-bat-outcome :outcome/walk [v] {:walks (inc (::bb v)) 
                                             :walk-rate (bb-rate v)})
(defmethod at-bat-outcome :outcome/k [v] {:k (inc (::k v)) :k-rate (k-rate v)})
(defmethod at-bat-outcome :outcome/single [v]
  (let [singles (inc (::singles v)) avg (/ (::hits v) (::ab v))]
    {:singles singles :batting-average avg}))

(defmulti dispatch-f class)
(defmethod dispatch-f clojure.lang.PersistentArrayMap [v]
  (at-bat-outcome v))
(defmethod dispatch-f clojure.lang.Keyword [_]
  (throw (ex-info "Did not pass spec :outcome/outcome" {})))

(dispatch-f (s/conform :outcome/outcome {:outcome/type :outcome/k
                                         ::bb 1
                                         ::pa 14
                                         ::k 4}))

