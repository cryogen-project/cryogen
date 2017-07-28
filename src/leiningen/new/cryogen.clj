(ns leiningen.new.cryogen
  (:require [leiningen.new.templates :refer [renderer sanitize year ->files]]
            [leinjacker.utils :refer [lein-generation]]
            [leiningen.core.main :as main]
            [clojure.java.io :as io]))

(defn resource [r]
  (->> r (str "leiningen/new/cryogen/") (io/resource) (io/input-stream)))

(def render (renderer "cryogen"))

(defn check-lein-version []
  (if (< (lein-generation) 2)
    (throw (new Exception "Leiningen v2 is required..."))))

(def blue-theme
  [["resources/templates/themes/blue/html/archives.html" (render "themes/blue/html/archives.html")]
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
   ["resources/templates/themes/blue/js/highlight.pack.js" (render "themes/blue/js/highlight.pack.js")]])

(def blue-centered-theme
  [["resources/templates/themes/blue_centered/html/archives.html" (render "themes/blue_centered/html/archives.html")]
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
   ["resources/templates/themes/blue_centered/js/highlight.pack.js" (render "themes/blue_centered/js/highlight.pack.js")]])

(def nucleus-theme
  [["resources/templates/themes/nucleus/html/archives.html" (render "themes/nucleus/html/archives.html")]
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
   ["resources/templates/themes/nucleus/css/menu.css" (render "themes/nucleus/css/menu.css")]
   ["resources/templates/themes/nucleus/css/reset.css" (render "themes/nucleus/css/reset.css")]
   ["resources/templates/themes/nucleus/css/style.css" (render "themes/nucleus/css/style.css")]
   ["resources/templates/themes/nucleus/css/typography.css" (render "themes/nucleus/css/typography.css")]])

(def lotus-theme
  [["resources/templates/themes/lotus/config.edn" (render "themes/lotus/config.edn")]
   ["resources/templates/themes/lotus/README.md" (render "themes/lotus/README.md")]
   ["resources/templates/themes/lotus/css/_buttons.scss" (render "themes/lotus/css/_buttons.scss")]
   ["resources/templates/themes/lotus/css/_layout.scss" (render "themes/lotus/css/_layout.scss")]
   ["resources/templates/themes/lotus/css/_menu.scss" (render "themes/lotus/css/_menu.scss")]
   ["resources/templates/themes/lotus/css/_typography.scss" (render "themes/lotus/css/_typography.scss")]
   ["resources/templates/themes/lotus/css/_variables.scss" (render "themes/lotus/css/_variables.scss")]
   ["resources/templates/themes/lotus/css/blog.scss" (render "themes/lotus/css/blog.scss")]
   ["resources/templates/themes/lotus/css/lotus-highlightjs.min.css" (render "themes/lotus/css/lotus-highlightjs.min.css")]
   ["resources/templates/themes/lotus/css/normalize.css" (render "themes/lotus/css/normalize.css")]
   ["resources/templates/themes/lotus/html/404.html" (render "themes/lotus/html/404.html")]
   ["resources/templates/themes/lotus/html/archives.html" (render "themes/lotus/html/archives.html")]
   ["resources/templates/themes/lotus/html/author.html" (render "themes/lotus/html/author.html")]
   ["resources/templates/themes/lotus/html/base.html" (render "themes/lotus/html/base.html")]
   ["resources/templates/themes/lotus/html/home.html" (render "themes/lotus/html/home.html")]
   ["resources/templates/themes/lotus/html/page.html" (render "themes/lotus/html/page.html")]
   ["resources/templates/themes/lotus/html/post.html" (render "themes/lotus/html/post.html")]
   ["resources/templates/themes/lotus/html/post-content.html" (render "themes/lotus/html/post-content.html")]
   ["resources/templates/themes/lotus/html/prev-next.html" (render "themes/lotus/html/prev-next.html")]
   ["resources/templates/themes/lotus/html/previews.html" (render "themes/lotus/html/previews.html")]
   ["resources/templates/themes/lotus/html/tag.html" (render "themes/lotus/html/tag.html")]
   ["resources/templates/themes/lotus/html/tags.html" (render "themes/lotus/html/tags.html")]
   ["resources/templates/themes/lotus/img/black-lotus.svg" (render "themes/lotus/img/black-lotus.svg")]
   ["resources/templates/themes/lotus/img/icons.svg" (render "themes/lotus/img/icons.svg")]
   ["resources/templates/themes/lotus/img/white-lotus.svg" (render "themes/lotus/img/white-lotus.svg")]
   ["resources/templates/themes/lotus/js/highlight.pack.js" (render "themes/lotus/js/highlight.pack.js")]])

(def themes
  (concat blue-theme
          blue-centered-theme
          nucleus-theme
          lotus-theme))

(defn cryogen
  "Create a new Cryogen project"
  [name]
  (check-lein-version)
  (let [options {:name      name
                 :sanitized (sanitize name)
                 :year      (year)}]
    (main/info "Generating fresh 'lein new' Cryogen project.")
    (with-redefs [leiningen.new.templates/render-text (fn [text _] text)]
      (apply ->files
             options
             [".gitignore" (render "gitignore")]
             ["project.clj" (render "project.clj")]
             ;;static resources
             ["resources/templates/img/cryogen.png" (resource "img/cryogen.png")]
             ["resources/templates/css/example.css" (resource "css/example.css")]
             ["resources/templates/css/sassexample.scss" (resource "css/sassexample.scss")]
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
             ["src/cryogen/server.clj" (render "src/cryogen/server.clj")]
             themes))))
