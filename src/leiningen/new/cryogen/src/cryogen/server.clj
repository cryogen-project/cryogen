(ns cryogen.server
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :as route]
            [ring.util.response :refer [redirect resource-response]]
            [ring.util.codec :refer [url-decode]]
            [cryogen-core.watcher :refer [start-watcher!]]
            [cryogen-core.plugins :refer [load-plugins]]
            [cryogen-core.compiler :refer [compile-assets-timed read-config]]
            [cryogen-core.io :refer [path]]))

(defn init []
  (load-plugins)
  (compile-assets-timed)
  (let [ignored-files (-> (read-config) :ignored-files)]
    (start-watcher! "resources/templates" ignored-files compile-assets-timed)))

(defn wrap-subdirectories
  [handler]
  (fn [request]
    (let [{:keys [clean-urls blog-prefix]} (read-config)
          req-uri (.substring (url-decode (:uri request)) 1)
          res-path (condp = clean-urls
                     :trailing-slash (path req-uri "index.html")
                     :no-trailing-slash (if (or (= req-uri "")
                                                (= req-uri "/")
                                                (= req-uri
                                                   (.substring blog-prefix 1)))
                                          (path req-uri "index.html")
                                          (path (str req-uri ".html")))
                     :dirty (path (str req-uri ".html")))]
      (or (resource-response res-path {:root "public"})
          (handler request)))))

(defroutes routes
  (GET "/" [] (redirect (let [config (read-config)]
                          (path (:blog-prefix config)
                                (when (= (:clean-urls config) :dirty)
                                  "index.html")))))
  (route/resources "/")
  (route/not-found "Page not found"))

(def handler (wrap-subdirectories routes))
