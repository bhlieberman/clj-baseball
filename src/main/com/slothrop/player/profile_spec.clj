(ns com.slothrop.player.profile-spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::games int?)

(s/def ::plate-appearances int?)

(s/def ::home-runs int?)

(s/def ::stolen-bases int?)

(s/def ::bb-rate double?)

(s/def ::k-rate double?)

(s/def ::iso float?)

(s/def ::babip float?)

(s/def ::avg float?)

(s/def ::slg float?)

(s/def ::wOBA float?)

(s/def ::wRC+ int?)

(s/def ::BsR float?)

(s/def ::Off float?)

(s/def ::Def float?)

(s/def ::WAR float?)

(s/def ::player-profile (s/keys :req-un [::games ::plate-appearances ::home-runs
                                 ::stolen-bases ::bb-rate ::k-rate ::iso
                                 ::babip ::avg ::obp ::slg ::wOBA ::wRC+
                                 ::BsR ::Off ::Def ::WAR]))

(s/def ::fangraphs-at-bats (s/map-of #{:pa :bb :hbp :sac :interference} 
                                     int?))