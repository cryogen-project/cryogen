(ns cryogen.io
  (:require [clojure.java.io :refer [file]]
            [me.raynes.fs :as fs]))

(def public "resources/public")

(defn get-resource [resource]
  (-> (Thread/currentThread)
      (.getContextClassLoader)
      (.getResource resource)
      (.toURI)
      (file)))

(defn find-assets [f ext]
  (->> (get-resource f)
       file-seq
       (filter (fn [file] (-> file .getName (.endsWith ext))))))

(defn create-folder [folder]
  (let [loc (file (str public folder))]
    (when-not (.exists loc)
      (.mkdirs loc))))

(defn copy-resources [blog-prefix]
  (let [css-template "resources/templates/css"
        css-public (str public blog-prefix "/css")
        js-template "resources/templates/js"
        js-public (str public blog-prefix "/js")]
    (fs/delete-dir css-public)
    (fs/delete-dir js-public)
    (fs/copy-dir css-template css-public)
    (fs/copy-dir js-template js-public)))