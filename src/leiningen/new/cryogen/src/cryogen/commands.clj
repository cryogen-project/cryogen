(ns cryogen.commands
  "command parsing from pages and posts written"
  (:require [cryogen.github :as git]
            [selmer.parser :refer [render]]
            [clojure.java.io :as io]))

(defn format-replace [r t] (prn r t)
  (render (slurp (io/resource (str "../resources/templates/html/layouts/" t)))
          r))

(defn command [r] 
  "command w/args to be parsed and formatted"
  (let [cmds {"git-src" #(-> (git/get-src %) (format-replace "git_src.html"))
              "git-gist" #(-> (git/get-gist %) (format-replace "git_gist.html"))}]
    (get cmds r)))

(defn find-and-call [pp]
  (for [_ (re-seq #"\{\{(.+?)}\}" pp)]
    (if-let [r (let [rem (clojure.string/split (second _) #"\s+")]
                 (when-let [cm (command (first rem))]
                   (if (rest rem)
                     (apply cm (rest rem))
                     (cm))))]
      {:match (first _) :replace r})))

(defn post-process [s]
  (loop [_pp s
         cm (find-and-call _pp)]
    (if (empty? cm)
      _pp
      (recur 
       (if-let [s (:match (first cm))]
         (clojure.string/replace _pp s
                                 (or (:replace (first cm))
                                     ""))
         _pp)
       (rest cm)))))
