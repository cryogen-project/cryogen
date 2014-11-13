(ns cryogen.server
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :as route]
            [ring.util.response :refer [redirect]]
            [cryogen.watcher :refer [start-watcher!]]
            [cryogen.compiler :refer [compile-assets-timed blog-prefix]]))

(defn init []
  (compile-assets-timed)
  (start-watcher! "resources/templates" compile-assets-timed))

(defroutes handler
  (GET "/" [] (redirect (str (blog-prefix) "/index.html")))
  (route/resources "/")
  (route/not-found "Page not found"))
