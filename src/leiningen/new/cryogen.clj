(ns leiningen.new.cryogen
  (:require [leiningen.new.templates :refer [renderer sanitize year ->files]]
            [leinjacker.utils :refer [lein-generation]]
            [leiningen.core.main :as main]
            [clojure.java.io :as io]))

(defn resource [r]
(->> r (str "leiningen/new/cryogen/") (io/resource) (io/input-stream)))

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
               ["resources/templates/img/cryogen.png" (resource "img/cryogen.png")]
               ;;themes
               ["resources/templates/themes/blue/html/archives.html" (render "themes/blue/html/archives.html")]
               ["resources/templates/themes/blue/html/author.html" (render "themes/blue/html/author.html")]
               ["resources/templates/themes/blue/html/base.html" (render "themes/blue/html/base.html")]
               ["resources/templates/themes/blue/html/home.html" (render "themes/blue/html/home.html")]
               ["resources/templates/themes/blue/html/page.html" (render "themes/blue/html/page.html")]
               ["resources/templates/themes/blue/html/post.html" (render "themes/blue/html/post.html")]
               ["resources/templates/themes/blue/html/post-content.html" (render "themes/blue/html/post-content.html")]
               ["resources/templates/themes/blue/html/previews.html" (render "themes/blue/html/previews.html")]
               ["resources/templates/themes/blue/html/tag.html" (render "themes/blue/html/tag.html")]
               ["resources/templates/themes/blue/html/tags.html" (render "themes/blue/html/tags.html")]
               ["resources/templates/themes/blue/html/404.html" (render "themes/blue/html/404.html")]
               ["resources/templates/themes/blue/html/view.html" (render "themes/blue/html/view.html")]
               ["resources/templates/themes/blue/html/sheet.html" (render "themes/blue/html/sheet.html")]
               ["resources/templates/themes/blue/css/screen.css" (render "themes/blue/css/screen.css")]
               ["resources/templates/themes/blue/css/gorilla-repl/output.css" (render "themes/blue/css/gorilla-repl/output.css")]
               ["resources/templates/themes/blue/css/gorilla-repl/viewer.css" (render "themes/blue/css/gorilla-repl/viewer.css")]
               ["resources/templates/themes/blue/css/gorilla-repl/worksheet.css" (render "themes/blue/css/gorilla-repl/worksheet.css")]
               ["resources/templates/themes/blue/js/highlight.pack.js" (render "themes/blue/js/highlight.pack.js")]
               ["resources/templates/themes/blue/js/gorilla-repl/jslib/d3/d3.v3.min.js" (render "themes/blue/js/gorilla-repl/jslib/d3/d3.v3.min.js")]
               ["resources/templates/themes/blue/js/gorilla-repl/jslib/d3/d3.geo.projection.min.js" (render "themes/blue/js/gorilla-repl/jslib/d3/d3.geo.projection.min.js")]
               ["resources/templates/themes/blue/js/gorilla-repl/jslib/uuid/uuid.core.js" (render "themes/blue/js/gorilla-repl/jslib/uuid/uuid.core.js")]
               ["resources/templates/themes/blue/js/gorilla-repl/jslib/vega/vega.1.3.3.min.js" (render "themes/blue/js/gorilla-repl/jslib/vega/vega.1.3.3.min.js")]
               ["resources/templates/themes/blue/js/gorilla-repl/js/worksheetParser.js" (render "themes/blue/js/gorilla-repl/js/worksheetParser.js")]
               ["resources/templates/themes/blue/js/gorilla-repl/js/utils.js" (render "themes/blue/js/gorilla-repl/js/utils.js")]
               ["resources/templates/themes/blue/js/gorilla-repl/js/mathJaxViewer.js" (render "themes/blue/js/gorilla-repl/js/mathJaxViewer.js")]
               ["resources/templates/themes/blue/js/gorilla-repl/js/outputViewer.js" (render "themes/blue/js/gorilla-repl/js/outputViewer.js")]
               ["resources/templates/themes/blue/js/gorilla-repl/js/renderer.js" (render "themes/blue/js/gorilla-repl/js/renderer.js")]
               ["resources/templates/themes/blue/js/gorilla-repl/js-viewer/segment-viewer.js" (render "themes/blue/js/gorilla-repl/js-viewer/segment-viewer.js")]
               ["resources/templates/themes/blue/js/gorilla-repl/js-viewer/worksheet-viewer.js" (render "themes/blue/js/gorilla-repl/js-viewer/worksheet-viewer.js")]
               ["resources/templates/themes/blue/js/gorilla-repl/js-viewer/github.js" (render "themes/blue/js/gorilla-repl/js-viewer/github.js")]
               ["resources/templates/themes/blue/js/gorilla-repl/js-viewer/bitbucket.js" (render "themes/blue/js/gorilla-repl/js-viewer/bitbucket.js")]
               ["resources/templates/themes/blue/js/gorilla-repl/js-viewer/main-viewer.js" (render "themes/blue/js/gorilla-repl/js-viewer/main-viewer.js")]

               ["resources/templates/themes/blue_centered/html/archives.html" (render "themes/blue_centered/html/archives.html")]
               ["resources/templates/themes/blue_centered/html/author.html" (render "themes/blue_centered/html/author.html")]
               ["resources/templates/themes/blue_centered/html/base.html" (render "themes/blue_centered/html/base.html")]
               ["resources/templates/themes/blue_centered/html/home.html" (render "themes/blue_centered/html/home.html")]
               ["resources/templates/themes/blue_centered/html/page.html" (render "themes/blue_centered/html/page.html")]
               ["resources/templates/themes/blue_centered/html/post.html" (render "themes/blue_centered/html/post.html")]
               ["resources/templates/themes/blue_centered/html/post-content.html" (render "themes/blue_centered/html/post-content.html")]
               ["resources/templates/themes/blue_centered/html/previews.html" (render "themes/blue_centered/html/previews.html")]
               ["resources/templates/themes/blue_centered/html/tag.html" (render "themes/blue_centered/html/tag.html")]
               ["resources/templates/themes/blue_centered/html/tags.html" (render "themes/blue_centered/html/tags.html")]
               ["resources/templates/themes/blue_centered/html/404.html" (render "themes/blue_centered/html/404.html")]
               ["resources/templates/themes/blue_centered/css/screen.css" (render "themes/blue_centered/css/screen.css")]
               ["resources/templates/themes/blue_centered/js/highlight.pack.js" (render "themes/blue_centered/js/highlight.pack.js")]
               ;;Markdown templates
               ["resources/templates/md/pages/about.md" (render "md/pages/about.md")]
               ["resources/templates/md/pages/another-page.md" (render "md/pages/another-page.md")]
               ["resources/templates/md/posts/2014-03-10-first-post.md" (render "md/posts/2014-03-10-first-post.md")]
               ["resources/templates/md/posts/2014-11-04-second-post.md" (render "md/posts/2014-11-04-second-post.md")]
               ["resources/templates/md/posts/2016-01-07-docs.md" (render "md/posts/2016-01-07-docs.md")]
               ["resources/templates/md/posts/2016-01-06-notebook.md" (render "md/posts/2016-01-06-notebook.md")]
               ;;Asciidoc templates
               ["resources/templates/asc/pages/adoc-page.asc" (render "asc/pages/adoc-page.asc")]
               ["resources/templates/asc/posts/2014-10-10-adoc-post.asc" (render "asc/posts/2014-10-10-adoc-post.asc")]
               ;;config
               ["resources/templates/config.edn" (render "config.edn")]
               ;;namespaces
               ["src/cryogen/core.clj" (render "src/cryogen/core.clj")]
               ["src/cryogen/server.clj" (render "src/cryogen/server.clj")]))))
