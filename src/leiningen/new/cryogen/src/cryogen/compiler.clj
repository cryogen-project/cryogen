(ns cryogen.compiler
  (:require [selmer.parser :refer [cache-off! render-file]]
            [cryogen.io :refer
             [get-resource find-assets create-folder wipe-public-folder copy-resources]]
            [cryogen.sitemap :as sitemap]
            [cryogen.rss :as rss]
            [io.aviso.exception :refer [write-exception]]
            [clojure.java.io :refer [copy file reader writer]]
            [clojure.string :as s]
            [text-decoration.core :refer :all]
            [markdown.core :refer [md-to-html-string]]
            [cryogen.toc :refer [generate-toc]]
            [cryogen.sass :as sass]))

(cache-off!)

(defn root-path [config k]
  (if-let [root (k config)]
    (str "/" root "/") "/"))

(def public "resources/public")

(defn find-md-assets []
  (find-assets "templates" ".md"))

(defn find-posts [{:keys [post-root]}]
  (find-assets (str "templates/md" post-root) ".md"))

(defn find-pages [{:keys [page-root]}]
  (find-assets (str "templates/md" page-root) ".md"))

(defn parse-post-date [file-name date-fmt]
  (let [fmt (java.text.SimpleDateFormat. date-fmt)]
    (.parse fmt (.substring file-name 0 10))))

(defn post-uri [file-name {:keys [blog-prefix post-root]}]
  (str blog-prefix post-root (s/replace file-name #".md" ".html")))

(defn page-uri [page-name {:keys [blog-prefix page-root]}]
  (str blog-prefix page-root (s/replace page-name #".md" ".html")))

(defn read-page-meta [page rdr]
  (try
    (read rdr)
    (catch Exception _
      (throw (IllegalArgumentException. (str "Malformed metadata on page: " page))))))

(defn parse-content [rdr]
  (md-to-html-string
    (->> (java.io.BufferedReader. rdr)
         (line-seq)
         (s/join "\n"))
    :heading-anchors true))

(defn parse-page [is-post? page config]
  (with-open [rdr (java.io.PushbackReader. (reader page))]
    (let [page-name (.getName page)
          file-name (s/replace page-name #".md" ".html")
          page-meta (read-page-meta page-name rdr)
          content (parse-content rdr)]
      (merge
        (update-in page-meta [:layout] #(str (name %) ".html"))
        {:file-name file-name
         :content   content
         :toc       (if (:toc page-meta) (generate-toc content))}
        (if is-post?
          (let [date (parse-post-date file-name (:post-date-format config))
                archive-fmt (java.text.SimpleDateFormat. "yyyy MMMM" (java.util.Locale. "en"))
                formatted-group (.format archive-fmt date)]
            {:date                    date
             :formatted-archive-group formatted-group
             :parsed-archive-group    (.parse archive-fmt formatted-group)
             :uri                     (post-uri file-name config)
             :tags                    (set (:tags page-meta))})
          {:uri        (page-uri file-name config)
           :page-index (:page-index page-meta)})))))

(defn read-posts [config]
  (->> (find-posts config)
       (map #(parse-page true % config))
       (sort-by :date)
       reverse))

(defn read-pages [config]
  (->> (find-pages config)
       (map #(parse-page false % config))
       (sort-by :page-index)))

(defn tag-post [tags post]
  (reduce (fn [tags tag]
            (update-in tags [tag] (fnil conj []) (select-keys post [:uri :title])))
          tags (:tags post)))

(defn group-by-tags [posts]
  (reduce tag-post {} posts))

(defn group-for-archive [posts]
  (->> posts
       (map #(select-keys % [:title :uri :date :formatted-archive-group :parsed-archive-group]))
       (group-by :formatted-archive-group)
       (map (fn [[group posts]]
              {:group        group
               :parsed-group (:parsed-archive-group (get posts 0))
               :posts        (map #(select-keys % [:title :uri :date]) posts)}))
       (sort-by :parsed-group)
       reverse))

(defn tag-info [{:keys [blog-prefix tag-root]} tag]
  {:name (name tag)
   :uri  (str blog-prefix tag-root (name tag) ".html")})

(defn add-prev-next [pages]
  (map (fn [[prev target next]]
         (assoc target
                :prev (if prev (select-keys prev [:title :uri]) nil)
                :next (if next (select-keys next [:title :uri]) nil)))
       (partition 3 1 (flatten [nil pages nil]))))

(defn group-pages [pages]
  (let [{navbar-pages  true
         sidebar-pages false} (group-by #(boolean (:navbar? %)) pages)]
    (map (partial sort-by :page-index) [navbar-pages sidebar-pages])))

(defn compile-pages [default-params pages {:keys [blog-prefix page-root]}]
  (when-not (empty? pages)
    (println (blue "compiling pages"))
    (create-folder (str blog-prefix page-root))
    (doseq [{:keys [uri] :as page} pages]
      (println "\t-->" (cyan uri))
      (spit (str public uri)
            (render-file "templates/html/layouts/page.html"
                         (merge default-params
                                {:servlet-context "../"
                                 :page            page}))))))

(defn compile-posts [default-params posts {:keys [blog-prefix post-root disqus-shortname]}]
  (when-not (empty? posts)
    (println (blue "compiling posts"))
    (create-folder (str blog-prefix post-root))
    (doseq [post posts]
      (println "\t-->" (cyan (:uri post)))
      (spit (str public (:uri post))
            (render-file (str "templates/html/layouts/" (:layout post))
                         (merge default-params
                                {:servlet-context  "../"
                                 :post             post
                                 :disqus-shortname disqus-shortname}))))))

(defn compile-tags [default-params posts-by-tag {:keys [blog-prefix tag-root] :as config}]
  (when-not (empty? posts-by-tag)
    (println (blue "compiling tags"))
    (create-folder (str blog-prefix tag-root))
    (doseq [[tag posts] posts-by-tag]
      (let [{:keys [name uri]} (tag-info config tag)]
        (println "\t-->" (cyan uri))
        (spit (str public uri)
              (render-file "templates/html/layouts/tag.html"
                           (merge default-params {:servlet-context "../"
                                                  :name            name
                                                  :posts           posts})))))))

(defn compile-index [default-params {:keys [blog-prefix disqus?]}]
  (println (blue "compiling index"))
  (spit (str public blog-prefix "/index.html")
        (render-file "templates/html/layouts/home.html"
                     (merge default-params
                            {:home true
                             :disqus?         disqus?
                             :post            (get-in default-params [:latest-posts 0])}))))

(defn compile-archives [default-params posts {:keys [blog-prefix]}]
  (println (blue "compiling archives"))
  (spit (str public blog-prefix "/archives.html")
        (render-file "templates/html/layouts/archives.html"
                     (merge default-params
                            {:archives true
                             :groups          (group-for-archive posts)}))))

(defn tag-posts [posts config]
  (map #(update-in % [:tags] (partial map (partial tag-info config))) posts))

(defn read-config []
  (let [config (-> "templates/config.edn"
                   get-resource
                   slurp
                   read-string
                   (update-in [:blog-prefix] (fnil str ""))
                   (update-in [:rss-name] (fnil str "rss.xml"))
                   (update-in [:sass-src] (fnil str "css"))
                   (update-in [:sass-dest] (fnil str "css"))
                   (update-in [:post-date-format] (fnil str "yyyy-MM-dd")))]
    (merge
      config
      {:page-root (root-path :page-root config)
       :post-root (root-path :post-root config)
       :tag-root  (root-path :tag-root config)})))

(defn compile-assets []
  (println (green "compiling assets..."))
  (let [{:keys [site-url blog-prefix rss-name recent-posts sass-src sass-dest] :as config} (read-config)
        posts (add-prev-next (read-posts config))
        pages (add-prev-next (read-pages config))
        [navbar-pages sidebar-pages] (group-pages pages)
        posts-by-tag (group-by-tags posts)
        posts (tag-posts posts config)
        default-params {:title         (:site-title config)
                        :tags          (map (partial tag-info config) (keys posts-by-tag))
                        :latest-posts  (->> posts (take recent-posts) vec)
                        :navbar-pages  navbar-pages
                        :sidebar-pages sidebar-pages
                        :archives-uri  (str blog-prefix "/archives.html")
                        :index-uri     (str blog-prefix "/index.html")
                        :rss-uri       (str blog-prefix "/" rss-name)}]

    (wipe-public-folder)
    (println (blue "copying resources"))
    (copy-resources config)
    (compile-pages default-params pages config)
    (compile-posts default-params posts config)
    (compile-tags default-params posts-by-tag config)
    (compile-index default-params config)
    (compile-archives default-params posts config)
    (println (blue "generating site map"))
    (spit (str public blog-prefix "/sitemap.xml") (sitemap/generate site-url))
    (println (blue "generating rss"))
    (spit (str public blog-prefix "/" rss-name) (rss/make-channel config posts))
    (println (blue "compiling sass"))
    (sass/compile-sass->css! sass-src sass-dest)))

(defn compile-assets-timed []
  (time
    (try
      (compile-assets)
      (catch Exception e
        (if
          (or (instance? IllegalArgumentException e)
              (instance? clojure.lang.ExceptionInfo e))
          (println (red "Error:") (yellow (.getMessage e)))
          (write-exception e))))))

(defn -main []
  (compile-assets-timed))
