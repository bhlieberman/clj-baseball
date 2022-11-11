(ns html-parsing.http.tests
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [html-parsing.http.client :refer [client send-request read-resp]])
  (:import [java.net.http HttpClient HttpRequest HttpResponse$BodyHandlers]
           [java.net URI]))

(deftest create-client
  (testing "that the HttpClient object works as expected"
    (let [client client
          uri (URI. "https://baseballsavant.mlb.com/statcast_search")
          req (.build (HttpRequest/newBuilder uri))
          resp (.send client req (HttpResponse$BodyHandlers/discarding))]
      (is (instance? HttpClient client))
      (is (= 200 (.statusCode resp))))))

(run-tests)