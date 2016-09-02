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
               ["resources/templates/themes/blue/css/screen.css" (render "themes/blue/css/screen.css")]
               ["resources/templates/themes/blue/js/highlight.pack.js" (render "themes/blue/js/highlight.pack.js")]

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

               ["resources/templates/themes/nucleus/html/archives.html" (render "themes/nucleus/html/archives.html")]
               ["resources/templates/themes/nucleus/html/author.html" (render "themes/nucleus/html/author.html")]
               ["resources/templates/themes/nucleus/html/base.html" (render "themes/nucleus/html/base.html")]
               ["resources/templates/themes/nucleus/html/home.html" (render "themes/nucleus/html/home.html")]
               ["resources/templates/themes/nucleus/html/page.html" (render "themes/nucleus/html/page.html")]
               ["resources/templates/themes/nucleus/html/post.html" (render "themes/nucleus/html/post.html")]
               ["resources/templates/themes/nucleus/html/post-content.html" (render "themes/nucleus/html/post-content.html")]
               ["resources/templates/themes/nucleus/html/previews.html" (render "themes/nucleus/html/previews.html")]
               ["resources/templates/themes/nucleus/html/tag.html" (render "themes/nucleus/html/tag.html")]
               ["resources/templates/themes/nucleus/html/tags.html" (render "themes/nucleus/html/tags.html")]
               ["resources/templates/themes/nucleus/html/404.html" (render "themes/nucleus/html/404.html")]
               ["resources/templates/themes/nucleus/js/highlight.pack.js" (render "themes/nucleus/js/highlight.pack.js")]
               ["resources/templates/themes/nucleus/js/scripts.js" (render "themes/nucleus/js/scripts.js")]
               ["resources/templates/themes/nucleus/css/buttons.css" (render "themes/nucleus/css/buttons.css")]
               ["resources/templates/themes/nucleus/css/highlight-tomorrow-night-eighties.css" (render "themes/nucleus/css/highlight-tomorrow-night-eighties.css")]
               ["resources/templates/themes/nucleus/css/menu.css" (render "themes/nucleus/css/menu.css")]
               ["resources/templates/themes/nucleus/css/reset.css" (render "themes/nucleus/css/reset.css")]
               ["resources/templates/themes/nucleus/css/style.css" (render "themes/nucleus/css/style.css")]
               ["resources/templates/themes/nucleus/css/typography.css" (render "themes/nucleus/css/typography.css")]
               ["resources/templates/themes/nucleus/css/fonts/fontawesome/font-awesome.css" (render "themes/nucleus/css/fonts/fontawesome/font-awesome.css")]
               ["resources/templates/themes/nucleus/css/fonts/fontawesome/FontAwesome.otf" (render "themes/nucleus/css/fonts/fontawesome/FontAwesome.otf")]
               ["resources/templates/themes/nucleus/css/fonts/fontawesome/fontawesome-webfont.eot" (render "themes/nucleus/css/fonts/fontawesome/fontawesome-webfont.eot")]
               ["resources/templates/themes/nucleus/css/fonts/fontawesome/fontawesome-webfont.svg" (render "themes/nucleus/css/fonts/fontawesome/fontawesome-webfont.svg")]
               ["resources/templates/themes/nucleus/css/fonts/fontawesome/fontawesome-webfont.ttf" (render "themes/nucleus/css/fonts/fontawesome/fontawesome-webfont.ttf")]
               ["resources/templates/themes/nucleus/css/fonts/fontawesome/fontawesome-webfont.woff" (render "themes/nucleus/css/fonts/fontawesome/fontawesome-webfont.woff")]

               ;;Markdown templates
               ["resources/templates/md/pages/about.md" (render "md/pages/about.md")]
               ["resources/templates/md/pages/another-page.md" (render "md/pages/another-page.md")]
               ["resources/templates/md/posts/2014-03-10-first-post.md" (render "md/posts/2014-03-10-first-post.md")]
               ["resources/templates/md/posts/2014-11-04-second-post.md" (render "md/posts/2014-11-04-second-post.md")]
               ["resources/templates/md/posts/2016-01-07-docs.md" (render "md/posts/2016-01-07-docs.md")]
               ;;Asciidoc templates
               ["resources/templates/asc/pages/adoc-page.asc" (render "asc/pages/adoc-page.asc")]
               ["resources/templates/asc/posts/2014-10-10-adoc-post.asc" (render "asc/posts/2014-10-10-adoc-post.asc")]
               ;;config
               ["resources/templates/config.edn" (render "config.edn")]
               ;;namespaces
               ["src/cryogen/core.clj" (render "src/cryogen/core.clj")]
               ["src/cryogen/server.clj" (render "src/cryogen/server.clj")]))))
