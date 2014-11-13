(ns cryogen.sitemap
  (:require [clojure.xml :refer [emit]]
            [cryogen.io :refer [get-resource find-assets]])
  (:import java.util.Date))

;;generate sitemaps using the sitemap spec
;;http://www.sitemaps.org/protocol.html

(defn format-date [date]
  (let [fmt (java.text.SimpleDateFormat. "yyyy-MM-dd")]
    (.format fmt date)))

(defn loc [f]
  (-> f (.getAbsolutePath) (.split "resources/public/") second))

(defn generate [site-url]
  (with-out-str
    (emit
      {:tag :urlset
       :attrs {:xmlns "http://www.sitemaps.org/schemas/sitemap/0.9"}
       :content
        (for [f (find-assets "public" ".html")]
         {:tag :url
          :content
          [{:tag :loc
            :content [(str site-url (loc f))]}
           {:tag :lastmod
            :content [(-> f (.lastModified) (Date.) format-date)]}]})})))
