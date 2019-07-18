cryogen-theme-wooji
===================

A simple theme for Cryogen

-------------------------------------------------------------------------------

<!-- markdown-toc start - Don't edit this section. Run M-x markdown-toc-refresh-toc -->
**Table of Contents**

- [cryogen-theme-wooji](#cryogen-theme-wooji)
    - [Setup](#setup)
    - [Demo](#demo)
    - [Contexts](#contexts)
        - [`content/config.edn`](#contentconfigedn)
        - [`content/md/posts/*.md`](#contentmdpostsmd)
        - [`content/md/pages/*.md`](#contentmdpagesmd)
    - [License](#license)

<!-- markdown-toc end -->

-------------------------------------------------------------------------------

Setup
-----

1. Clone `cryogen-theme-wooji` on `themes` directory as `wooji`:

    ``` bash
    $ git clone https://github.com/ejelome/cryogen-theme-wooji.git themes/wooji
    ```

2. Change `:theme` to `wooji` in `content/config.edn`:

    ``` clojure
    :theme "wooji"
    ```

-------------------------------------------------------------------------------

Demo
----

See personal [homepage](https://ejelome.com).

-------------------------------------------------------------------------------

Contexts
--------

You will need to supply the following custom `keyword`s on `content/` files to make `wooji` work as expected.

### `content/config.edn` ###

``` clojure
{
 ...
 :previews? true                             ; (required): use previews.html as home
 ...
 :wooji {:favicon {:href "/img/favicon.ico"  ; (required): favicon path (absolute path)
                   :type ""                  ; (optional): mime type of favicon
                                             ;  [default]: "image/x-icon"
                   :rel ""}                  ; (optional): rel type of favicon
                                             ;  [default]: "shortcut icon"
         :logo {:src    "/img/logo.png"      ; (required): logo path (absolute path)
                :alt    ""                   ; (optional): alt of logo if broken
                                             ;  [default]: :site-title
                :width  nil                  ; (optional): width of logo
                                             ;  [default]: 64
                :height nil}                 ; (optional): height of logo
                                             ;  [default]: 64
         :social-medias [{:icon  ""          ; (required): icon of socia media
                          :href  ""          ; (optional): url of social media
                          :title ""}]        ; (optional): title of social media
         :label {:recent-posts        ""     ; (optional): heading of homepage posts
                                             ;  [default]: "Recent posts"
                 :post-summary        ""     ; (optional): heading of :post-summary
                                             ;  [default]: "Summary"
                 :author-html         ""     ; (optional): heading of author.html
                                             ;  [default]: "Posts published by"
                 :archive-description ""     ; (optional): meta-description of archives.html
                                             ;  [default]: "Archives of posts according to date."
                 :tags-description    ""     ; (optional): meta-description of tags.html
                                             ;  [default]: "List of tags used on posts"
                 :tag-html            ""}}   ; (optional): heading of tag.html
                                             ;  [default]: "Posts tagged with"
 ...
}
```

### `content/md/posts/*.md` ###

``` clojure
{
 ...
 :featured?   true ; (required): include post as featured on homepage (use only on a single post)
 :description ""   ; (required): meta-description of post for SERPs (trimmed at 160th character)
 :excerpt     ""   ; (required): excerpt of post on homepage (trimmed at 150th character)
 :summary     ""   ; (required): summary of post (treated as single a paragraph)
 ...
}
```

### `content/md/pages/*.md` ###

``` clojure
{
 ...
 :description "" ; (required): meta-description of page for SERPs (trimmed at 160th character)
 ...
}
```

-------------------------------------------------------------------------------

License
-------

`cryogen-theme-wooji` is licensed under [MIT](./LICENSE)
