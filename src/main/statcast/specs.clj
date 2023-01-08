(ns main.statcast.specs
  (:require [clojure.spec.alpha :as s]))

(s/def ::pitch-type (s/coll-of string?))
(s/def ::pitch-result (s/coll-of string?))
(s/def ::batted-ball-location (s/coll-of string?))
(s/def ::count (s/coll-of pos-int?))
(s/def ::player-type string?) ;; enums in spec?
(s/def ::pitcher-handedness string?)
(s/def ::game-date-after inst?)
(s/def ::team string?)
(s/def ::position string?)
(s/def ::inning (s/coll-of int?))
(s/def ::flags (s/coll-of string?))
(s/def ::metric-range string?)
(s/def ::group-by string?) ;; metadata to be removed from main part of spec
(s/def ::min-pa int?)
(s/def ::pa-result (s/coll-of string?))
(s/def ::gameday-zones (s/coll-of int?))
(s/def ::attack-zones (s/coll-of int?))
(s/def ::season (s/coll-of int?))
(s/def ::outs (s/coll-of int?))
(s/def ::batter-handedness string?)
(s/def ::game-date-before inst?)
(s/def ::home-or-away string?)
(s/def ::infield-alignment (s/coll-of string?))
(s/def ::batted-ball-type (s/coll-of string?))
(s/def ::min-pitches pos-int?)
(s/def ::sort-by string?)
(s/def ::season-type (s/coll-of string?))
(s/def ::venue (s/coll-of string?))
(s/def ::batted-ball-dir (s/coll-of string?))
(s/def ::situation (s/coll-of string?))
(s/def ::opponent (s/coll-of string?))
(s/def ::quality-of-contact (s/coll-of string?))
(s/def ::month (s/coll-of string?))
(s/def ::runners-on (s/coll-of string?))
(s/def ::of-alignment (s/coll-of string?))
(s/def ::batters string?)
(s/def ::pitchers string?)
(s/def ::min-results int?) ;; metadata
(s/def ::sort-order int?) ;; metadata

(s/def ::query (s/keys :opt-un [::pitch-type ::pitch-result ::batted-ball-location
                                ::count ::player-type ::pitcher-handedness
                                ::game-date-after ::team ::position ::inning ::flags
                                ::metric-range ::group-by ::min-pa ::pa-result
                                ::gameday-zones ::attack-zones ::season
                                ::outs ::batter-handedness ::game-date-before
                                ::home-or-away ::infield-alignment ::batted-ball-type
                                ::min-pitches ::sort-by ::season-type ::venue
                                ::batted-ball-dir ::situation ::opponent
                                ::quality-of-contact ::month ::runners-on
                                ::of-alignment ::batters ::pitchers
                                ::min-results ::sort-order]))

(s/valid? ::query {:pitch-type [""] :pitcher-handedness "foo"})