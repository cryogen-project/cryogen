(ns cryogen.watcher
  (:require [clojure.java.io :refer [file]]))

(defn get-assets [root]
  (file-seq (file root)))

(defn sum-times [path]
  (->> (get-assets path) (map #(.lastModified %)) (reduce +)))

(defn watch-assets [root action]
  (loop [times (sum-times root)]
    (Thread/sleep 300)
    (let [new-times (sum-times root)]
      (when-not (= times new-times)
        (action))
      (recur new-times))))

(defn start-watcher! [root action]
  (doto (Thread. #(watch-assets root action))
    (.setDaemon true)
    (.start)))
