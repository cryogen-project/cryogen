(ns cryogen.github
  (:require [cheshire.core :as json])
  (:import (org.apache.commons.codec.binary Base64 StringUtils)))

(defn get-gist [gist-uri]
  (let [gist-id (last (clojure.string/split gist-uri #"/+")) ;;just need id for git api
        gist-resp (try (slurp (str "https://api.github.com/gists/" gist-id))
                       (catch Exception e {:error (.getMessage e)}))]

    (when-not (:error gist-resp)
      (if-let [gist (-> (json/parse-string gist-resp)
                        (get "files")
                        first ;;todo: optionally get all gist files?
                        val)]

        {:content (get gist "content")
         :language (get gist "language")
         :name (get gist "filename")
         :id gist-id}))))

(defn get-src [git-file]
  (let [git-re  (re-find  #"github.com/(.*)/blob/(.+?)/(.+)" git-file) ;;want second and last now (user/repo,file) for git api
        git-res (str "https://api.github.com/repos/" (second git-re) "/contents/" (last git-re))
        git-resp (try (slurp git-res)
                       (catch Exception e {:error (.getMessage e)}))]
    (when-not (:error git-resp)
      (if-let [git-src (json/parse-string git-resp)]
        {:content (String. (Base64/decodeBase64 (get git-src "content")) "UTF-8")
         :name (get git-src "name")
         :uri (get (get git-src "_links") "html")}))))


(defn get-gits-ex []
  [(get-gist "https://gist.github.com/viperscape/cec68f0791687f5959f1")
   (get-src "https://github.com/viperscape/kuroshio/blob/master/examples/pubsub.clj")])

;(prn (get-gits-ex))
