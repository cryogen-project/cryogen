<img src="https://raw.githubusercontent.com/lacarmen/cryogen/master/cryogen.png"
 hspace="20" align="left"height="200"/>


## Features

* blog posts and pages with Markdown
* Theming support with Twitter Bootstrap
* plain HTML page templates
* code syntax highlighting
* tags
* sitemap
* RSS

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

The web server can be started using the `lein-ring` plugin:

```
lein ring server
```

The server will watch for changes in the `resources/templates` folder and recompile the content automatically.

### Site Configuration

The site configuration file is found at `resources/config.edn`, this file looks as follows:

```clojure
{:site-title "My Awesome Blog"
 :author "Bob Bobbert"
 :description "This blog is awesome"
 :site-url "http://blogawesome.com/"
 :post-root "posts"
 :tag-root "tags"
 :page-root "pages"
 :tags? true
 :blog-prefix nil
 :rss-name "feed.xml"}  ;if set to nil, it will default to rss.xml
```

### Creating Posts

The posts are located in the `resources/templates/md/posts`. Posts are written using Markdown and each post file
should start with the date in the format of `dd-MM-yyyy`. The compiler will link the posts in order for you using
the dates. A valid post file might look as follows:

```
19-12-2014-post1.md
```

The post content must start with a map containing the post metadata:

```clojure
{:title "First Post!"
 :layout :post
 :tags  ["tag1" "tag3"]}
```

The metadata contains the following keys:

* `:title` - the title of the post
* `:layout` - the layout template to use for the post
* `:tags` - the tags associated with this post

The rest of the post should consist of valid Markdown content, eg:

```
## Hello World

This is my first post!

check out this sweet code

    (defn foo [bar]
      (bar))

Lorem ipsum dolor sit amet, consectetur adipiscing elit.
Nunc sodales pharetra massa, eget fringilla ex ornare et.
Nunc mattis diam ac urna finibus sodales. Etiam sed ipsum
et purus commodo bibendum. Cras libero magna, fringilla
tristique quam sagittis, volutpat auctor mi. Aliquam luctus,
nulla et vestibulum finibus, nibh justo semper tortor, nec
vestibulum tortor est nec nisi.
```

### Creating Pages

Pages work similarly to posts, but aren't grouped by date. An example page might be an about page.

The pages contain the following metadata:

* `:title` - the title of the page
* `:layout` - the layout template for the page
* `:page-index` - a number representing the order of the page in the navbar/sidebar
* `:navbar?` - determines whether the page should be shown in the navbar, `false` by default

### Customizing Layouts

The layouts are contained in the `resources/templates/html/layouts` folder of the project. By default, the `base.html`
layout is used to provide the general layout for the site. This is where you would add static resources such as CSS and Js
assets as well as define headers and footers for your site.

Each page layout should have a name that matches the `:layout` key in the page metadata and end with `.html`. Page layouts
extend the base layout and should only contain the content relaveant to the page inside the `content` block.
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

The ` initHighlightingOnLoad` function is called with jQuery in `templates/html/layouts/base.html`.

```xml
<script>
    $(document).ready(function() {
        $('pre').each(function(i, block) {
            hljs.highlightBlock(block);
        });
    });
</script>
```

## Deploying Your Site

The generated static content will be found under the `resources/public` folder. Simply copy the content to a static
folder for a server sugh as Nginx or Apache and your site is now ready for service.

## Sites Made With Cryogen

* [My personal blog](http://carmenla.me/blog/index.html)

## License

Copyright © 2014 Carmen La

Distributed under the Eclipse Public License, the same as Clojure.
