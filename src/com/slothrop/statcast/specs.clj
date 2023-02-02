(ns com.slothrop.statcast.specs
  (:require [clojure.spec.alpha :as s])
  (:import [java.util Date]
           [java.time Instant]))

(s/def ::fastballs (s/coll-of #{:two-seam :four-seam :cutter :sinker} :distinct true))

(s/def ::offspeed (s/coll-of #{::split-finger ::changeup
                               ::forkball ::screwball} :distinct true))

(s/def ::breaking (s/coll-of #{:slider :curveball :knuckle-curve
                               :slow-curve :knuckleball :eephus} :distinct true))

(s/def ::pitch-type (s/or :pitches (s/coll-of #{:two-seam :four-seam
                                                :cutter :sinker
                                                :split-finger :slider
                                                :changeup :curveball
                                                :knuckle-curve :slow-curve
                                                :knuckleball :forkball
                                                :eephus :screwball
                                                :intentional-ball :pitchout
                                                :automatic-ball :unknown} :distinct true)
                          :fastballs ::fastballs
                          :offspeed ::offspeed
                          :breaking-balls ::breaking))

(s/def ::swing-and-miss (s/coll-of #{:foul-tip :swinging-pitchout
                                     :swinging-strike :swinging-strike-blocked} :distinct true))

(s/def ::in-play (s/coll-of #{:in-play-out :in-play-no-out
                              :in-play-run :in-play-pitchout-run} :distinct true))

(s/def ::fouls (s/coll-of #{:foul :foul-pitchout} :distinct true))

(s/def ::all-swings (s/and ::swing-and-miss ::in-play ::fouls))

(s/def ::pitch-result (s/or :all (s/and :automatic-ball :ball :ball-in-dirt
                                        :called-strike :foul :foul-bunt :foul-pitchout
                                        :pitchout :hit-by-pitch :intent-ball :in-play-out
                                        :in-play-no-out :in-play-run
                                        :in-play-pitchout-run :missed-bunt :foul-tip
                                        :swinging-pitchout :swinging-strike)
                            :swing-and-miss ::swing-and-miss
                            :in-play ::in-play
                            :fouls ::fouls
                            :all-swings ::all-swings))

(s/def ::batted-ball-location (s/coll-of #{:pitcher :catcher :first-base :second-base
                                           :third-base :short-stop
                                           :left-field :center-field :right-field} :distinct true))

(s/def ::count (s/map-of #{:0-0 :0-1 :0-2
                           :1-0 :1-1 :1-2
                           :2-0 :2-1 :2-2
                           :3-0 :3-1 :3-2
                           :ahead-in-count-hitter :even-count
                           :behind-in-count-hitter :2-strikes :3-balls} boolean?))

(s/def ::player-type string?) ;; enums in spec?

(s/def ::pitcher-handedness string?)

(s/def ::game-date-gt (s/nilable (s/or :as-str string? :as-date #(instance? java.time.LocalDate %))))

(s/def ::american-league (s/coll-of  #{:blue-jays :orioles :rays
                                       :red-sox :yankees :guardians
                                       :royals :tigers :twins
                                       :white-sox :angels :astros
                                       :athletics :mariners :rangers}
                                     :kind set?
                                     :distinct true
                                     :count 15))

(s/def ::national-league (s/coll-of #{:braves :marlins :mets
                                      :nationals :phillies :brewers
                                      :cardinals :cubs :pirates
                                      :reds :d-backs :dodgers
                                      :giants :padres :rockies}
                                    :kind set?
                                    :distinct true
                                    :count 15))

(s/def ::team (s/or :teams (s/coll-of #{:blue-jays :orioles :rays
                                        :red-sox :yankees :guardians
                                        :royals :tigers :twins
                                        :white-sox :angels :astros
                                        :athletics :mariners :rangers
                                        :braves :marlins :mets
                                        :nationals :phillies :brewers
                                        :cardinals :cubs :pirates
                                        :reds :d-backs :dodgers
                                        :giants :padres :rockies} :kind set?)
                    :leagues (s/or :al ::american-league :nl ::national-league)))

(s/def ::position (s/nilable string?))

(s/def ::inning (s/map-of #{:1 :2 :3
                            :4 :5 :6
                            :7 :8 :9
                            :extra-innings} boolean?))

(s/def ::flags (s/map-of #{:is-putout
                           :is-basehit
                           :is-inside-the-park-hr
                           :is-out-of-the-park-hr
                           :is-hard-hit
                           :is-bunt
                           :is-last-pitch
                           :is-sweet-spot
                           :pos.-player-pitching
                           :remove-bunts
                           :starting-pos-player?
                           :non-starting-pos-player?
                           :is-rookie-batter
                           :is-rookie-pitcher} boolean?))

(s/def ::metric-range string?)

(s/def ::group-by string?) ;; metadata to be removed from main part of spec

(s/def ::min-pa int?)

(s/def ::base-hit (s/map-of #{:single :double :triple :home-run} true?))

(s/def ::outs (s/map-of #{:field-out :strikeout
                          :strikeout-double-play :double-play :gidp :fielders-choice
                          :fielders-choice-out :force-out :sac-bunt :sac-bunt-double-play :sac-fly
                          :sac-fly-double-play :triple-play} true?))

(s/def ::pa-result (s/or :all (s/map-of #{:single :double :triple
                                          :home-run :field-out :strikeout
                                          :strikeout-double-play :walk :double-play
                                          :field-error :gidp :fielders-choice
                                          :fielders-choice-out :batter-interference :catcher-interference
                                          :caught-stealing-2b :caught-stealing-3b :caught-stealing-home
                                          :force-out :hit-by-pitch :intentional-walk
                                          :sac-bunt :sac-bunt-double-play :sac-fly
                                          :sac-fly-double-play :triple-play} boolean?)
                         :base-hit ::base-hit
                         :outs ::outs))

(s/def ::gameday-zones (s/map-of #{:1 :2 :3
                                   :4 :5 :6
                                   :7 :8 :9
                                   :11 :12 :13 :14} boolean?))

(def xf (map (comp keyword str)))

(s/def ::heart (s/map-of (into #{} xf (range 1 10)) true?))

(s/def ::shadow (s/map-of (into #{} xf (range 11 20)) true?))

(s/def ::chase (s/map-of (into #{} xf (range 21 30)) true?))

(s/def ::waste (s/map-of (into #{} xf (range 31 40)) true?))

(s/def ::attack-zones (s/or :all (s/map-of (into #{} xf (range 1 40)) boolean?)
                            :heart ::heart
                            :shadow ::shadow
                            :chase ::chase
                            :waste ::waste))

(s/def ::statcast-season (s/map-of (into #{} xf (range 2015 2023)) true?))

(s/def ::pitch-tracking (s/map-of (into #{} xf (range 2008 2023)) true?))

(s/def ::season (s/map-of (into #{} xf (range 2008 2023)) boolean?))

(s/def ::outs (s/map-of #{:0 :1 :2 :3} boolean?))

(s/def ::batter-handedness string?)

(s/def ::game-date-lt (s/nilable (s/or :as-str string? :as-date #(instance? java.time.LocalDate %))))

(s/def ::home-or-away string?)

(s/def ::infield-alignment (s/map-of #{:standard :strategic :shift} boolean?))

(s/def ::batted-ball-type (s/map-of #{:flyball :popup :line-drive :ground-ball} boolean?))

(s/def ::min-pitches int?)

(s/def ::sort-by string?)

(s/def ::season-type (s/map-of #{:regular-season :playoffs :wildcard
                                 :division-series :league-championship :world-series
                                 :spring-training :all-star} boolean?))

(s/def ::venue (s/map-of #{:az-chase-field :atl-truist-park :atl-2016-turner-field
                           :bal-oriole-park :bos-fenway-park :chc-wrigley-field
                           :cin-gabp :cle-progressive-field :col-coors-field
                           :cws-guaranteed-rate-fld :det-comerica-park :fla-2011-hard-rock-stadium
                           :hou-minute-maid-park :kc-kauffman-stadium :laa-angel-stadium
                           :lad-dodger-stadium :mia-marlins-park :mil-american-family-field
                           :min-target-field :min-2009-metrodome :nym-citi-field
                           :nym-2008-shea-stadium :nyy-yankee-stadium :nyy-2008-yankee-stadium
                           :oak-oakland-coliseum :phi-citizens-bank-park :pit-pnc-park
                           :sd-petco-park :sea-t-mobile-park :sf-oracle-park
                           :stl-busch-stadium :tb-tropicana-field :tex-globe-life-field
                           :tex-2019-globe-life-park :tor-rogers-centre :wsh-nationals-park} boolean?))

(s/def ::batted-ball-dir (s/map-of #{:pull :straight-away :opposite} boolean?))

(s/def ::situation (s/map-of #{:go-ahead-run-at-plate
                               :go-ahead-run-on-base
                               :tying-run-at-plate
                               :tying-run-on-base
                               :tying-run-on-deck} boolean?))

(s/def ::opponent ::team)

(s/def ::quality-of-contact (s/coll-of string?))

(s/def ::month (s/map-of #{:mar-apr
                           :may
                           :jun
                           :jul
                           :aug
                           :sep-oct} boolean?))

(s/def ::runners-on (s/map-of #{:no-runners
                                :risp
                                :runner-on-base
                                :runner-on-1st
                                :runner-on-2nd
                                :runner-on-3rd
                                :runner-not-on-1st
                                :runner-not-on-2nd
                                :runner-not-on-3rd} boolean?))

(s/def ::of-alignment (s/map-of #{:standard
                                  :strategic
                                  :3-of-to-one-side-of-2b
                                  :4th-outfielder} boolean?))

(s/def ::batters string?)

(s/def ::pitchers string?)

(s/def ::min-results int?) ;; metadata

(s/def ::sort-order string?) ;; metadata

(s/def ::query (s/keys :opt-un [::pitch-type ::pitch-result ::batted-ball-location
                                ::count ::player-type ::pitcher-handedness
                                ::game-date-gt ::team ::position ::inning ::flags
                                ::metric-range ::group-by ::min-pa ::pa-result
                                ::gameday-zones ::attack-zones ::season
                                ::outs ::batter-handedness ::game-date-lt
                                ::home-or-away ::infield-alignment ::batted-ball-type
                                ::min-pitches ::sort-by ::season-type ::venue
                                ::batted-ball-dir ::situation ::opponent
                                ::quality-of-contact ::month ::runners-on
                                ::of-alignment ::batters ::pitchers
                                ::min-results ::sort-order]))
