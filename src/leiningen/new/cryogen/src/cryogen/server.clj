(ns cryogen.server
  (:require
    [compojure.core :refer [GET defroutes]]
    [compojure.route :as route]
    [ring.util.response :refer [redirect file-response]]
    [ring.util.codec :refer [url-decode]]
    [ring.server.standalone :as ring-server]
    [cryogen-core.watcher :refer [start-watcher! start-watcher-for-changes!]]
    [cryogen-core.plugins :refer [load-plugins]]
    [cryogen-core.compiler :refer [compile-assets-timed]]
    [cryogen-core.config :refer [resolve-config]]
    [cryogen-core.io :refer [path]]
    [clojure.string :as string])
  (:import (java.io File)))

(def resolved-config (delay (resolve-config)))

(def extra-config-dev
  "Add dev-time configuration overrides here, such as `:hide-future-posts? false`"
  {})

(defn init [& fast?]
  (load-plugins)
  (compile-assets-timed extra-config-dev)
  (let [ignored-files (-> @resolved-config :ignored-files)]
    (run!
      #(if fast?
         (start-watcher-for-changes! % ignored-files compile-assets-timed extra-config-dev)
         (start-watcher! % ignored-files compile-assets-timed))
      ["content" "themes"])))

(defn wrap-subdirectories
  [handler]
  (fn [request]
    (let [{:keys [clean-urls blog-prefix public-dest]} @resolved-config
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
                                                     (if (string/blank? blog-prefix)
                                                       blog-prefix
                                                       (.substring blog-prefix 1))))
                                            (path req-uri "index.html")
                                            (path (str req-uri ".html")))
                       :dirty (path (str req-uri ".html")))
                     req-uri)]
        (or (let [rsp (file-response res-path {:root public-dest})
                  body (:body rsp)]
              ;; Add content-type; it cannot be derived from the extension if `:[no-]trailing-slash`
              (cond-> rsp
                      (and body
                           (instance? File body)
                           (string/ends-with? (.getName body) ".html"))
                      (assoc-in [:headers "Content-Type"] "text/html")))
            (handler request)))))

(defroutes routes
  (GET "/" [] (redirect (let [config (resolve-config)]
                          (path (:blog-prefix config)
                                (when (= (:clean-urls config) :dirty)
                                  "index.html")))))
  (route/files "/")
  (route/not-found "Page not found"))

(def handler (wrap-subdirectories routes))

(defn serve
  "Entrypoint for running via tools-deps (clojure)"
  [{:keys [fast join?] :as opts}]
  (ring-server/serve
    handler
    (merge
      {:join? (if (some? join?) join? true)
       :init (partial init fast)
       :open-browser? true
       :auto-refresh? fast ; w/o fast it would often try to reload the page before it has been fully compiled
       :refresh-paths [(:public-dest @resolved-config)]}
      opts)))

(defn -main [& args]
  (serve {:port 3000 :fast ((set args) "fast")}))

(comment
  (def srv (serve {:join? false, :fast true}))
  (.stop srv)

  ,)
