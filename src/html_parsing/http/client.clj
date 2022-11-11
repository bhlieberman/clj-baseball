(ns html-parsing.http.client 
  (:import [java.net.http HttpClient
            HttpRequest
            HttpResponse$BodyHandlers]
           [java.io InputStream]))

(defonce client (HttpClient/newHttpClient))

(defn send-request [^HttpRequest req]
  (.send client req (HttpResponse$BodyHandlers/ofInputStream)))

(defn read-resp ^InputStream [resp]
  (.body resp))
