(ns com.slothrop.clj-baseball.bbref.datasource
  (:import [org.jsoup Jsoup]
           [java.time LocalDateTime]
           [java.time.temporal ChronoUnit]))

(def ^:private session (-> (Jsoup/newSession)
                           (.userAgent "Mozilla")))

(def ^:private time-since-last-request (atom nil))

(defn -get [url]
  (if @time-since-last-request
    (let [delta (.until @time-since-last-request (LocalDateTime/now) ChronoUnit/SECONDS)
          sleep-time (- 6 delta)]
      (when (pos-int? sleep-time)
        (Thread/sleep sleep-time)))
    (reset! time-since-last-request (LocalDateTime/now)))
  (.. session newRequest (url url) get))
  
