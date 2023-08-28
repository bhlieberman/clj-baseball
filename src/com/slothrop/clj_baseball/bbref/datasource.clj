(ns com.slothrop.clj-baseball.bbref.datasource
  (:import [org.jsoup Jsoup]
           [java.time LocalDateTime]
           [java.time.temporal ChronoUnit]))

(def session (-> (Jsoup/newSession)
                 (.userAgent "Mozilla")))

(def time-since-last-request (atom nil))

(defn -get [url]
  (if @time-since-last-request
    (let [delta (.until @time-since-last-request (LocalDateTime/now) ChronoUnit/SECONDS)
          sleep-time (- 6 delta)]
      (when (pos-int? sleep-time)
        (Thread/sleep sleep-time)))
    (reset! time-since-last-request (LocalDateTime/now)))
  (.. session newRequest (url url) get))

(comment
  #_(binding [time-since-last-request (LocalDateTime/of 2023 8 28 9 17 56 30)]
    (.until time-since-last-request (LocalDateTime/now) ChronoUnit/SECONDS)))