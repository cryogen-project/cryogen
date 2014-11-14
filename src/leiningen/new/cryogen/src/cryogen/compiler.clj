(ns cryogen.compiler
  (:require [selmer.parser :refer [cache-off! render-file]]
            [cryogen.io :refer [get-resource find-assets create-folder copy-resources]]
            [cryogen.sitemap :as sitemap]
            [cryogen.rss :as rss]
            [io.aviso.exception :refer [write-exception]]
            [clojure.java.io :refer [copy file reader writer]]
            [clojure.string :as s]
            [text-decoration.core :refer :all]
            [markdown.core :refer [md-to-html-string]]))

(cache-off!)

(def config (-> "config.edn" get-resource slurp read-string))

(defn blog-prefix []
  (if-let [prefix (:blog-prefix config)]
    (str "/" prefix)
    ""))

(defn root-path [k]
  (if-let [root (k config)]
    (str "/" root "/") "/"))

(def public "resources/public")
(def page-root (root-path :page-root))
(def post-root (root-path :post-root))
(def tag-root (root-path :tag-root))

(defn find-md-assets []
  (find-assets "templates" ".md"))

(defn find-posts []
  (find-assets (str "templates/md" post-root) ".md"))

(defn find-pages []
  (find-assets (str "templates/md" page-root) ".md"))

(defn parse-post-date [file-name]
  (let [fmt (java.text.SimpleDateFormat. "dd-MM-yyyy")]
    (.parse fmt (.substring file-name 0 (.lastIndexOf file-name "-")))))

(defn post-uri [file-name]
  (str (blog-prefix) post-root (s/replace file-name #".md" ".html")))

(defn page-uri [page-name]
  (str (blog-prefix) page-root (s/replace page-name #".md" ".html")))

(defn read-page-meta [page rdr]
  (try
    (read rdr)
    (catch Exception _
      (throw (IllegalArgumentException. (str "Malformed metadata on page: " page))))))

(defn parse-page [is-post? page]
  (with-open [rdr (java.io.PushbackReader. (reader page))]
    (let [page-name (.getName page)
          file-name (s/replace page-name #".md" ".html")
          r (read-page-meta page-name rdr)]
      (merge
        (update-in r [:layout] #(str (name %) ".html"))
        {:file-name file-name
         :content   (->> (java.io.BufferedReader. rdr)
                         (line-seq)
                         (s/join "\n")
                         md-to-html-string)}
        (if is-post?
          {:date          (parse-post-date file-name)
           :archive-group (.format (java.text.SimpleDateFormat. "yyyy MMMM") (parse-post-date file-name))
           :uri           (post-uri file-name)
           :tags          (set (:tags r))}
          {:uri        (page-uri file-name)
           :page-index (:page-index r)})))))

(defn read-posts []
  (->> (find-posts)
       (map #(parse-page true %))
       (sort-by :date)
       reverse))

(defn read-pages []
  (->> (find-pages)
       (map #(parse-page false %))
       (sort-by :page-index)))

(defn tag-post [tags post]
  (reduce (fn [tags tag]
            (update-in tags [tag] (fnil conj []) (select-keys post [:uri :title])))
          tags (:tags post)))

(defn group-by-tags [posts]
  (reduce tag-post {} posts))

(defn group-for-archive [posts]
  (->> posts
       (map #(select-keys % [:archive-group :title :uri :date]))
       (group-by :archive-group)
       (map (fn [[group posts]] {:group group :posts posts}))))

(defn tag-info [tag]
  {:name (name tag)
   :uri  (str (blog-prefix) tag-root (name tag) ".html")})

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

(defn compile-pages [default-params pages]
  (when-not (empty? pages)
    (println (blue "compiling pages"))
    (create-folder (str (blog-prefix) page-root))
    (doseq [page pages]
      (println "\t-->" (cyan (:uri page)))
      (spit (str public (:uri page))
            (render-file "templates/html/layouts/page.html"
                         (merge default-params
                                {:servlet-context "../"
                                 :page            page}))))))

(defn compile-posts [default-params posts]
  (when-not (empty? posts)
    (println (blue "compiling posts"))
    (create-folder (str (blog-prefix) post-root))
    (doseq [post posts]
      (println "\t-->" (cyan (:uri post)))
      (spit (str public (:uri post))
            (render-file (str "templates/html/layouts/" (:layout post))
                         (merge default-params
                                {:servlet-context "../"
                                 :post            post}))))))

(defn compile-tags [default-params posts-by-tag]
  (when-not (empty? posts-by-tag)
    (println (blue "compiling tags"))
    (create-folder (str (blog-prefix) tag-root))
    (doseq [[tag posts] posts-by-tag]
      (let [{:keys [name uri]} (tag-info tag)]
        (println "\t-->" (cyan uri))
        (spit (str public uri)
              (render-file "templates/html/layouts/tag.html"
                           (merge default-params {:servlet-context "../"
                                                  :name            name
                                                  :posts           posts})))))))

(defn compile-index [default-params]
  (println (blue "compiling index"))
  (spit (str public (blog-prefix) "/index.html")
        (render-file "templates/html/layouts/home.html"
                     (merge default-params
                            {:servlet-context ""
                             :post            (get-in default-params [:latest-posts 0])}))))

(defn compile-archives [default-params posts]
  (println (blue "compiling archives"))
  (spit (str public (blog-prefix) "/archives.html")
        (render-file "templates/html/layouts/archives.html"
                     (merge default-params
                            {:servlet-context (str "")
                             :groups          (group-for-archive posts)}))))

(defn tag-posts [posts]
  (map #(update-in % [:tags] (partial map tag-info)) posts))

(defn compile-assets []
  (println (green "compiling assets..."))
  (let [posts (add-prev-next (read-posts))
        pages (add-prev-next (read-pages))
        [navbar-pages sidebar-pages] (group-pages pages)
        posts-by-tag (group-by-tags posts)
        posts (tag-posts posts)
        rss-name (if-let [rssn (:rss-name config)] rssn "rss.xml")
        default-params {:title         (:site-title config)
                        :tags          (map tag-info (keys posts-by-tag))
                        :latest-posts  (->> posts (take 2) vec)
                        :navbar-pages  navbar-pages
                        :sidebar-pages sidebar-pages
                        :archives-uri  (str (blog-prefix) "/archives.html")
                        :index-uri     (str (blog-prefix) "/index.html")
                        :rss-uri       (str (blog-prefix) "/" rss-name)}]
    (println (blue "copying resources"))
    (copy-resources (blog-prefix))
    (compile-pages default-params pages)
    (compile-posts default-params posts)
    (compile-tags default-params posts-by-tag)
    (compile-index default-params)
    (compile-archives default-params posts)
    (println (blue "generating site map"))
    (spit (str public (blog-prefix) "/sitemap.xml") (sitemap/generate (:site-url config)))
    (println (blue "generating rss"))
    (spit (str public (blog-prefix) "/" rss-name) (rss/make-channel config posts))))

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
