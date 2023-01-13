(ns com.slothrop.player.profile
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is run-tests]]))

(defprotocol Stats
  (get-stats [this profile])
  (compute-advanced-stats [this]))

(defprotocol FindData
  (get-player-profile [this]))

(defrecord PlayerProfile [name age team stats]
  FindData
  (get-player-profile [this]
    (let [[first-name last-name] (str/split (:name this) #"\s")
          id (str (take 5 last-name) (take 2 first-name) 01)]
      (assoc this :id id))))

(defrecord PlayerStats [season games plate-appearances
                        at-bats runs hits doubles triples
                        home-runs RBI stolen-bases caught-stealing
                        base-on-balls strikeouts batting-avg on-base-percentage
                        slugging OPS OPS+ took-base grounded-double-play hit-by-pitch
                        sac-bunts sac-flies IBB])

(extend-protocol Stats
  PlayerStats
  (get-stats [this profile] (assoc profile :stats this))
  (compute-advanced-stats [this] this))

(deftest player-profile-lookup
  (let [profile (->PlayerProfile "Cedric Mullins" 28 "Baltimore Orioles" {})]
    (is (= (get-player-profile profile) "MulliCe01"))))


(let [name "Cedric Mullins"
      [first-name last-name] (str/split name #"\s")
      first-name (->> first-name
           .toLowerCase
           (take 2)
           (str/join ""))
      last-name (->> last-name
                     .toLowerCase
                     (take 5)
                     (str/join ""))]
  (str last-name first-name "01"))

(run-tests)