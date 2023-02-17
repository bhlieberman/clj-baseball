(ns com.slothrop.player.lookup
  (:require [cognitect.aws.client.api :as aws]
            [cognitect.aws.credentials :as creds]
            [clojure.pprint :as pp]
            [clojure.java.io :as jio]
            [charred.api :refer [read-csv]]))

(def s3-client (aws/client {:api :s3
                            :credentials-provider
                            (creds/profile-credentials-provider "jamf")}))

(aws/invoke s3-client {:op :CreateBucket
                       :request {:Bucket "clj-baseball"
                                 :ACL "public-read"
                                 :CreateBucketConfiguration
                                 {:LocationConstraint "us-west-2"}}})

(aws/invoke s3-client {:op :PutObject
                       :request {:ACL "public-read"
                                 :Bucket "clj-baseball"
                                 :Key "lookup-table"
                                 :ContentType "text/plain"
                                 :Body (jio/input-stream "/home/slothrop/Downloads/lookup-table.csv")}})

(def lookup-table
  (->>
   {:op :GetObject
    :request {:Bucket "clj-baseball"
              :Key "lookup-table"}}
   (aws/invoke s3-client)
   :Body
   read-csv))

(def keep-cols (keep (fn [[& x]] (map (partial nth x) 
                                      (conj (into [] (range 1 11)) 15)))))

(defmulti player-profile identity)
(defmethod player-profile :cols [_] (mapcat identity (eduction (take 1) keep-cols lookup-table)))
(defmethod player-profile :rows [_] (transduce (comp (drop 1) keep-cols) conj lookup-table))

(player-profile :cols)
(player-profile :rows)