(ns cryogen.toc
 (:require [crouton.html :as html]
           [hiccup.core :as hiccup]))

(defn get-headings [content]
  (reduce
    (fn [headings {:keys [tag attrs content] :as elm}]
      (if (some #{tag} [:h1 :h2 :h3])
        (conj headings elm)
        (if-let [more-headings (get-headings content)]
          (into headings more-headings)
          headings)))
    [] content))

(defn make-links [headings]
  (into [:ol.contents]
        (for [{[{{name :name} :attrs} title] :content} headings]
          [:li [:a {:href (str "#" name)} title]])))

(defn generate-toc [html]
  (-> html
      (.getBytes)
      (java.io.ByteArrayInputStream.)
      (html/parse)
      :content
      (get-headings)
      (make-links)
      (hiccup/html)))
