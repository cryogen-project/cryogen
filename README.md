<img src="https://raw.githubusercontent.com/lacarmen/cryogen/master/cryogen.png"
 hspace="20" align="left" height="200"/>

<!-- Non-breaking space -->
&nbsp;

This ReadMe only documents a subset of Cryogen's features. For additional documentation please see the [cryogen site](http://cryogenweb.org).

<!-- Non-breaking space -->
&nbsp;


## Features

* Blog posts and pages with Markdown (default) or AsciiDoc
* Tags
* Table of contents generation
* Plain HTML page templates
* Code syntax highlighting
* Disqus support
* Sitemap generation
* RSS feed generation
* Sass/SCSS compilation

## Prerequisites

You will need [Leiningen][1] 2.5.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Usage

### Creating a New Site

A new site can be created using the Cryogen template as follows:

```
lein new cryogen my-blog
```

### Running the Server

The web server can be started from the `my-blog` directory using the `lein-ring` plugin:

```
lein ring server
```

The server will watch for changes in the `resources/templates` folder and recompile the content automatically.

### Site Configuration

The site configuration file is found at `templates/config.edn`, this file looks as follows:

```clojure
{:site-title         "My Awesome Blog"
 :author             "Bob Bobbert"
 :description        "This blog is awesome"
 :site-url           "http://blogawesome.com/"
 :post-root          "posts"
 :page-root          "pages"
 :post-root-uri      "posts-output"
 :page-root-uri      "pages-output"
 :tag-root-uri       "tags-output"
 :author-root-uri    "authors-output"
 :blog-prefix        "/blog"
 :rss-name           "feed.xml"
 :rss-filters        ["cryogen"]
 :recent-posts       3
 :post-date-format   "yyyy-MM-dd"
 :sass-src           nil
 :sass-dest          nil
 :theme              "blue"
 :resources          ["img"]
 :keep-files         [".git"]
 :disqus?            false
 :disqus-shortname   ""
 :ignored-files      [#"\.#.*" #".*\.swp$"]
 :posts-per-page     5
 :blocks-per-preview 2
 :previews?          false
 :clean-urls?        true}
```

For information about each key please see the ["Configuration"](http://cryogenweb.org/docs/configuration.html) portion of the Cryogen documentation site.

### Switching between Markdown and AsciiDoc

Cryogen comes with Markdown support as default. If you want to use AsciiDoc instead, open the `project.clj` in your created blog (e.g. `my-blog`), and change the line in `:dependencies` that says `cryogen-markdown` to `cryogen-asciidoc`.
Instead of looking for files ending in `.md` in the `resources/templates/md` directory, the compiler will now look for files ending in `.asc` in the `resources/templates/asc` directory.

### Selecting a Theme

The Cryogen template comes with three themes in the `resources/templates/themes` folder. To change your blog's theme, change the value of the `:theme` key in `config.edn`.
Note that the Nuclues theme is obtained from [downloadwebsitetemplates.co.uk](http://www.downloadwebsitetemplates.co.uk/template/nucleus/) that requires you to keep the footer, unless you make a donation on their website.

### Customizing Layouts

Cryogen uses [Selmer](https://github.com/yogthos/Selmer) templating engine for layouts. Please refer to its documentation
to see the supported tags and filters for the layouts.

The layouts are contained in the `resources/templates/themes/{theme}/html` folder of the project. By default, the `base.html` layout is used to provide the general layout for the site. This is where you would add static resources such as CSS and JavaScript
assets as well as define headers and footers for your site.

Each page layout should have a name that matches the `:layout` key in the page metadata and end with `.html`. Page layouts extend the base layout and should only contain the content relaveant to the page inside the `content` block.
For example, the `tag` layout is located in `tag.html` and looks as follows:

```xml
{% extends "templates/html/layouts/base.html" %}
{% block content %}
<div id="posts-by-tag">
    <h2>Posts tagged {{name}}</h2>
    <ul>
    {% for post in posts %}
        <li>
            <a href="{{post.uri}}">{{post.title}}</a>
        </li>
    {% endfor %}
    </ul>
</div>
{% endblock %}
```

### Code Syntax Highlighting

Cryogen uses [Highlight.js](https://highlightjs.org/) for code syntax highlighting. You can add more languages by replacing `templates/js/highlight.pack.js` with a customized package from [here](https://highlightjs.org/download/).

The ` initHighlightingOnLoad` function is called in `{theme}/html/base.html`.

```xml
<script>hljs.initHighlightingOnLoad();</script>
```

## Deploying Your Site

The generated static content will be found under the `resources/public` folder. Simply copy the content to a static
folder for a server such as Nginx or Apache and your site is now ready for service.

A sample Nginx configuration that's placed in `/etc/nginx/sites-available/default` can be seen below:

```javascript
server {
  listen 80 default_server;
  listen [::]:80 default_server ipv6only=on;
  server_name localhost <yoursite.com> <www.yoursite.com>;

  access_log  /var/log/blog_access.log;
  error_log   /var/log/blog_error.log;

  location / {
    alias       /var/blog/;
    error_page  404 = /404.html;
  }
}
```

Simply set `yoursite.com` to the domain of your site in the above configuration and
ensure the static content is available at `/var/blog/`. Finally, place your custom error page
in the `/var/blog/404.html` file.

More information on deployment can be found [here](http://cryogenweb.org/docs/deploying-to-github-pages.html).

## Third Party Libraries

#### https://github.com/greywolve/cryogen-markdown-external

A Clojure library to provide Markdown rendering to the cryogen-core compiler by using an external command/program, such as pandoc.



## Some Sites Made With Cryogen

* [My personal blog](http://carmenla.me/blog/archives)
* [Cryogen Documentation Site](http://cryogenweb.org)
* [Yogthos' blog](http://yogthos.net/)
* [Clojure :in Tunisia](http://www.clojure.tn)
* [dl1ely.github.io](http://dl1ely.github.io)
* [nil/recur](http://jonase.github.io/nil-recur)
* [on the clojure move](http://tangrammer.github.io/)
* [AGYNAMIX Site & Blog](http://www.agynamix.de)
* [e-Resident Me](http://eresident.me)
* [Chad Stovern's blog](http://www.chadstovern.com)
* [Greative](https://greative.jp/)
* [LambdaX](http://lambdax.io/blog/)
* [szcz](http://www.szcz.org/)
* [himmAllRight's blog](http://ryan.himmelwright.net)
* [Clojurians](http://blog.clojurians.org)
* [Gundee and Company](http://www.gundee.com/)
* [Teamcool Rocks](http://www.teamcool.net/index.html)

## License

Copyright © 2014-2016 Carmen La

Distributed under the Eclipse Public License, the same as Clojure.
