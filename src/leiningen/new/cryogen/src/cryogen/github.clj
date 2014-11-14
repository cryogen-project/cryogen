(ns cryogen.github
  (:require [cheshire.core :as json])
  (:import (org.apache.commons.codec.binary Base64 StringUtils)))

(defn get-gist [gist-uri]
  (let [gist-id (last (clojure.string/split gist-uri #"/+"))
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
  (let [gist-resp (try (slurp git-file)
                       (catch Exception e {:error (.getMessage e)}))]
    (when-not (:error gist-resp)
      (if-let [git-src (json/parse-string gist-resp)]
        {:content (String. (Base64/decodeBase64 (get git-src "content")) "UTF-8")
         :name (get git-src "name")
         :uri (get (get git-src "_links") "html")}))))


(defn get-gits-ex []
  [(get-gist "https://gist.github.com/viperscape/cec68f0791687f5959f1")
   (get-src "https://api.github.com/repos/viperscape/rust-irc/contents/src/main.rs")])

;(prn (get-gits-ex))
