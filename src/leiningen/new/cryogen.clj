(ns leiningen.new.cryogen
  (:require [leiningen.new.templates :refer [renderer sanitize year ->files]]
            [leinjacker.utils :refer [lein-generation]]
            [leiningen.core.main :as main]
            [clojure.java.io :refer [file]]))

(defn check-lein-version []
  (if (< (lein-generation) 2)
    (throw (new Exception "Leiningen v2 is required..."))))

(defn cryogen
  "Create a new Cryogen project"
  [name]
  (check-lein-version)
  (let [options {:name name
                 :sanitized (sanitize name)
                 :year (year)}
        render (renderer "cryogen")]
    (main/info "Generating fresh 'lein new' Cryogen project.")
    (with-redefs [leiningen.new.templates/render-text (fn [text _] text)]
      (->files options
               [".gitignore"  (render "gitignore")]
               ["project.clj" (render "project.clj")]
               ;;static resources
               ["resources/templates/css/screen.css" (render "css/screen.css")]
               ["resources/templates/js/highlight.pack.js" (render "js/highlight.pack.js")]
               ;;HTML templates
               ["resources/templates/html/layouts/archives.html" (render "html/layouts/archives.html")]
               ["resources/templates/html/layouts/base.html" (render "html/layouts/base.html")]
               ["resources/templates/html/layouts/home.html" (render "html/layouts/home.html")]
               ["resources/templates/html/layouts/page.html" (render "html/layouts/page.html")]
               ["resources/templates/html/layouts/post.html" (render "html/layouts/post.html")]
               ["resources/templates/html/layouts/tag.html" (render "html/layouts/tag.html")]
               ;;Markdown templates
               ["resources/templates/md/pages/about.md" (render "md/pages/about.md")]
               ["resources/templates/md/pages/another-page.md" (render "md/pages/another-page.md")]
               ["resources/templates/md/posts/10-03-2014-first-post.md" (render "md/posts/10-03-2014-first-post.md")]
               ["resources/templates/md/posts/11-04-2014-second-post.md" (render "md/posts/11-04-2014-second-post.md")]
               ["resources/templates/md/posts/13-11-2014-docs.md" (render "md/posts/13-11-2014-docs.md")]
               ;;Asciidoc templates
               ["resources/templates/asc/pages/adoc-page.asc" (render "asc/pages/adoc-page.asc")]
               ["resources/templates/asc/posts/10-12-2014-adoc-post.asc" (render "asc/posts/10-12-2014-adoc-post.asc")]
               ;;config
               ["resources/templates/config.edn" (render "config.edn")]
               ;;namespaces
               ["src/cryogen/compiler.clj" (render "src/cryogen/compiler.clj")]
               ["src/cryogen/markup.clj" (render "src/cryogen/markup.clj")]
               ["src/cryogen/toc.clj" (render "src/cryogen/toc.clj")]
               ["src/cryogen/io.clj" (render "src/cryogen/io.clj")]
               ["src/cryogen/rss.clj" (render "src/cryogen/rss.clj")]
               ["src/cryogen/server.clj" (render "src/cryogen/server.clj")]
               ["src/cryogen/sitemap.clj" (render "src/cryogen/sitemap.clj")]
               ["src/cryogen/watcher.clj" (render "src/cryogen/watcher.clj")]
               ["src/cryogen/github.clj" (render "src/cryogen/github.clj")]
               ["src/cryogen/sass.clj" (render "src/cryogen/sass.clj")]))))
