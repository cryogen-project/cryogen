(ns cryogen.server
  (:require [clojure.java.io :refer [as-file resource]]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :as route]
            [ring.util.response :refer [content-type]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [cryogen-core.watcher :refer [start-watcher!]]
            [cryogen-core.plugins :refer [load-plugins]]
            [cryogen-core.compiler :refer [compile-assets-timed read-config]]
            [cryogen-core.io :refer [path]]))

(defn init []
  (load-plugins)
  (compile-assets-timed)
  (let [ignored-files (-> (read-config) :ignored-files)]
    (start-watcher! "resources/templates" ignored-files compile-assets-timed)))

(defroutes routes
  (route/resources "/")
  (route/not-found "Page not found"))

(defn wrap-default-content-type [handler]
  (fn [request]
    (-> (handler request)
        (content-type "text/html"))))

(def handler (-> routes
                 (wrap-file
                   (-> "public" resource as-file str)
                   {:index-files? (:clean-urls? (read-config))})
                 (wrap-default-content-type)
                 (wrap-defaults site-defaults)))
