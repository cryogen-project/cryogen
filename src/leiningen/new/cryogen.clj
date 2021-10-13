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
  [["themes/blue/html/archives.html" (render "themes/blue/html/archives.html")]
   ["themes/blue/html/author.html" (render "themes/blue/html/author.html")]
   ["themes/blue/html/base.html" (render "themes/blue/html/base.html")]
   ["themes/blue/html/home.html" (render "themes/blue/html/home.html")]
   ["themes/blue/html/page.html" (render "themes/blue/html/page.html")]
   ["themes/blue/html/post.html" (render "themes/blue/html/post.html")]
   ["themes/blue/html/post-content.html" (render "themes/blue/html/post-content.html")]
   ["themes/blue/html/previews.html" (render "themes/blue/html/previews.html")]
   ["themes/blue/html/tag.html" (render "themes/blue/html/tag.html")]
   ["themes/blue/html/tags.html" (render "themes/blue/html/tags.html")]
   ["themes/blue/html/404.html" (render "themes/blue/html/404.html")]
   ["themes/blue/css/screen.css" (render "themes/blue/css/screen.css")]
   ["themes/blue/js/highlight.pack.js" (render "themes/blue/js/highlight.pack.js")]])

(def blue-centered-theme
  [["themes/blue_centered/html/archives.html" (render "themes/blue_centered/html/archives.html")]
   ["themes/blue_centered/html/author.html" (render "themes/blue_centered/html/author.html")]
   ["themes/blue_centered/html/base.html" (render "themes/blue_centered/html/base.html")]
   ["themes/blue_centered/html/home.html" (render "themes/blue_centered/html/home.html")]
   ["themes/blue_centered/html/page.html" (render "themes/blue_centered/html/page.html")]
   ["themes/blue_centered/html/post.html" (render "themes/blue_centered/html/post.html")]
   ["themes/blue_centered/html/post-content.html" (render "themes/blue_centered/html/post-content.html")]
   ["themes/blue_centered/html/previews.html" (render "themes/blue_centered/html/previews.html")]
   ["themes/blue_centered/html/tag.html" (render "themes/blue_centered/html/tag.html")]
   ["themes/blue_centered/html/tags.html" (render "themes/blue_centered/html/tags.html")]
   ["themes/blue_centered/html/404.html" (render "themes/blue_centered/html/404.html")]
   ["themes/blue_centered/css/screen.css" (render "themes/blue_centered/css/screen.css")]
   ["themes/blue_centered/js/highlight.pack.js" (render "themes/blue_centered/js/highlight.pack.js")]])

(def nucleus-theme
  [["themes/nucleus/html/archives.html" (render "themes/nucleus/html/archives.html")]
   ["themes/nucleus/html/author.html" (render "themes/nucleus/html/author.html")]
   ["themes/nucleus/html/base.html" (render "themes/nucleus/html/base.html")]
   ["themes/nucleus/html/home.html" (render "themes/nucleus/html/home.html")]
   ["themes/nucleus/html/page.html" (render "themes/nucleus/html/page.html")]
   ["themes/nucleus/html/post.html" (render "themes/nucleus/html/post.html")]
   ["themes/nucleus/html/post-content.html" (render "themes/nucleus/html/post-content.html")]
   ["themes/nucleus/html/previews.html" (render "themes/nucleus/html/previews.html")]
   ["themes/nucleus/html/tag.html" (render "themes/nucleus/html/tag.html")]
   ["themes/nucleus/html/tags.html" (render "themes/nucleus/html/tags.html")]
   ["themes/nucleus/html/404.html" (render "themes/nucleus/html/404.html")]
   ["themes/nucleus/js/highlight.pack.js" (render "themes/nucleus/js/highlight.pack.js")]
   ["themes/nucleus/js/scripts.js" (render "themes/nucleus/js/scripts.js")]
   ["themes/nucleus/css/buttons.css" (render "themes/nucleus/css/buttons.css")]
   ["themes/nucleus/css/menu.css" (render "themes/nucleus/css/menu.css")]
   ["themes/nucleus/css/reset.css" (render "themes/nucleus/css/reset.css")]
   ["themes/nucleus/css/style.css" (render "themes/nucleus/css/style.css")]
   ["themes/nucleus/css/typography.css" (render "themes/nucleus/css/typography.css")]])

(def lotus-theme
  [["themes/lotus/config.edn" (render "themes/lotus/config.edn")]
   ["themes/lotus/README.md" (render "themes/lotus/README.md")]
   ["themes/lotus/css/_buttons.scss" (render "themes/lotus/css/_buttons.scss")]
   ["themes/lotus/css/_layout.scss" (render "themes/lotus/css/_layout.scss")]
   ["themes/lotus/css/_menu.scss" (render "themes/lotus/css/_menu.scss")]
   ["themes/lotus/css/_typography.scss" (render "themes/lotus/css/_typography.scss")]
   ["themes/lotus/css/_variables.scss" (render "themes/lotus/css/_variables.scss")]
   ["themes/lotus/css/blog.scss" (render "themes/lotus/css/blog.scss")]
   ["themes/lotus/css/lotus-highlightjs.min.css" (render "themes/lotus/css/lotus-highlightjs.min.css")]
   ["themes/lotus/css/normalize.css" (render "themes/lotus/css/normalize.css")]
   ["themes/lotus/html/404.html" (render "themes/lotus/html/404.html")]
   ["themes/lotus/html/archives.html" (render "themes/lotus/html/archives.html")]
   ["themes/lotus/html/author.html" (render "themes/lotus/html/author.html")]
   ["themes/lotus/html/base.html" (render "themes/lotus/html/base.html")]
   ["themes/lotus/html/home.html" (render "themes/lotus/html/home.html")]
   ["themes/lotus/html/page.html" (render "themes/lotus/html/page.html")]
   ["themes/lotus/html/post.html" (render "themes/lotus/html/post.html")]
   ["themes/lotus/html/post-content.html" (render "themes/lotus/html/post-content.html")]
   ["themes/lotus/html/prev-next.html" (render "themes/lotus/html/prev-next.html")]
   ["themes/lotus/html/previews.html" (render "themes/lotus/html/previews.html")]
   ["themes/lotus/html/tag.html" (render "themes/lotus/html/tag.html")]
   ["themes/lotus/html/tags.html" (render "themes/lotus/html/tags.html")]
   ["themes/lotus/img/black-lotus.svg" (render "themes/lotus/img/black-lotus.svg")]
   ["themes/lotus/img/icons.svg" (render "themes/lotus/img/icons.svg")]
   ["themes/lotus/img/white-lotus.svg" (render "themes/lotus/img/white-lotus.svg")]
   ["themes/lotus/js/highlight.pack.js" (render "themes/lotus/js/highlight.pack.js")]])

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
             [".gitignore" (render "root/gitignore")]
             ["project.clj" (render "root/project.clj")]
             ["deps.edn" (render "root/deps.edn")]
             ;;static resources
             ["content/img/cryogen.png" (resource "content/img/cryogen.png")]
             ["content/css/example.css" (resource "content/css/example.css")]
             ["content/css/sassexample.scss" (resource "content/css/sassexample.scss")]
             ;;Markdown templates
             ["content/md/pages/about.md" (render "content/md/pages/about.md")]
             ["content/md/pages/another-page.md" (render "content/md/pages/another-page.md")]
             ["content/md/posts/2014-03-10-first-post.md" (render "content/md/posts/2014-03-10-first-post.md")]
             ["content/md/posts/2014-11-04-second-post.md" (render "content/md/posts/2014-11-04-second-post.md")]
             ["content/md/posts/2020-12-03-docs.md" (render "content/md/posts/2020-12-03-docs.md")]
             ;;Asciidoc templates
             ["content/asc/pages/adoc-page.asc" (render "content/asc/pages/adoc-page.asc")]
             ["content/asc/posts/2014-10-10-adoc-post.asc" (render "content/asc/posts/2014-10-10-adoc-post.asc")]
             ;;config
             ["content/config.edn" (render "content/config.edn")]
             ;;namespaces
             ["src/cryogen/core.clj" (render "src/cryogen/core.clj")]
             ["src/cryogen/server.clj" (render "src/cryogen/server.clj")]
             themes))))
