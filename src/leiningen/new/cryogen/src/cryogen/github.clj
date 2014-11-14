(ns cryogen.github
  (:require [cheshire.core :as json]))

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

(get-gist "https://gist.github.com/viperscape/cec68f0791687f5959f1")

