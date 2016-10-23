(ns leiningen.prep-dirs
  (:use [clojure.java.io :as io]))

(defn prep-dirs [project]
  (doseq [path (get-in project [:ring :refresh-paths])]
    (let [dir (-> path io/as-file)]
      (when-not (.exists dir)
        (io/make-parents dir)
        (.mkdir dir)))))
