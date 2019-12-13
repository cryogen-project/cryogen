(ns cryogen.server
  (:require 
   [clojure.string :as string]
   [compojure.core :refer [GET defroutes]]
   [compojure.route :as route]
   [ring.util.response :refer [redirect file-response]]
   [ring.util.codec :refer [url-decode]]
   [cryogen-core.watcher :refer [start-watcher!]]
   [cryogen-core.plugins :refer [load-plugins]]
   [cryogen-core.compiler :refer [compile-assets-timed]]
   [cryogen-core.config :refer [resolve-config]]
   [cryogen-core.io :refer [path]]))

(defn init []
  (load-plugins)
  (compile-assets-timed)
  (let [ignored-files (-> (resolve-config) :ignored-files)]
    (start-watcher! "content" ignored-files compile-assets-timed)
    (start-watcher! "themes" ignored-files compile-assets-timed)))

(defn wrap-subdirectories
  [handler]
  (fn [request]
    (let [{:keys [clean-urls blog-prefix public-dest]} (resolve-config)
          req-uri (.substring (url-decode (:uri request)) 1)
          res-path (if (or (.endsWith req-uri "/")
                           (.endsWith req-uri ".html")
                           (-> (string/split req-uri #"/")
                               last
                               (string/includes? ".")
                               not))
                     (condp = clean-urls
                       :trailing-slash (path req-uri "index.html")
                       :no-trailing-slash (if (or (= req-uri "")
                                                  (= req-uri "/")
                                                  (= req-uri
                                                     (.substring blog-prefix 1)))
                                            (path req-uri "index.html")
                                            (path (str req-uri ".html")))
                       :dirty (path (str req-uri ".html")))
                     req-uri)]
      (or (file-response res-path {:root public-dest})
          (handler request)))))

(defroutes routes
  (GET "/" [] (redirect (let [config (resolve-config)]
                          (path (:blog-prefix config)
                                (when (= (:clean-urls config) :dirty)
                                  "index.html")))))
  (route/files "/")
  (route/not-found "Page not found"))

(def handler (wrap-subdirectories routes))
