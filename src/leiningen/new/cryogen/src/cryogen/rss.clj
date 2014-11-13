(ns cryogen.rss
  (:require [clj-rss.core :as rss]
            [clojure.xml :refer [emit]])
  (:import java.util.Date))


(defn posts-to-items [site-url author posts]
  (map
    (fn [{:keys [uri title content date]}]
      (let [link (str (if (.endsWith site-url "/") (apply str (butlast site-url)) site-url) uri)]
        {:guid        link
         :link        link
         :title       title
         :description content
         :pubDate     date
         :author      author}))
    posts))

(defn make-channel [config posts]
  (apply
    (partial rss/channel-xml
             false
             {:title         (:site-title config)
              :link          (:site-url config)
              :description   (:description config)
              :lastBuildDate (Date.)
              :author        (:author config)})
    (posts-to-items (:site-url config) (:author config) posts)))