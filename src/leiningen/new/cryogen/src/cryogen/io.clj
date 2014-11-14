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

(defn wipe-public-folder []
  (doseq [path (.listFiles (file public))]
    (fs/delete-dir path)))

(defn copy-resources [{:keys [blog-prefix resources]}]
  (doseq [resource resources]
    (fs/copy-dir
      (str "resources/templates/" resource)
      (str public blog-prefix "/" resource))))
