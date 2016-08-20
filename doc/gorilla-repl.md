# Including a Gorilla-repl worksheet as a blog post

To embed a Gorilla-repl worksheet as a blog post, here's what one needs to do:

* Create a new post in the posts folder in the usual fashion by naming prefixing the date. e.g. 2016-01-06-notebook.md
* An example of the header section of 2016-01-06-notebook.md

```

{:title "Gorilla-repl Notebook"
 :layout :sheet
 :page-index 0
 :source "github"
 :user "JonyEpsilon"
 :tags ["Gorilla-repl worksheet example"]
 :repo "gorilla-test"
 :path "ws/graph-examples.clj"}

```

## Headers to note:

* The :layout field should be set to :sheet
* The :source should be set to github. To load a sheet from BitBucket or Github gist, one needs to change the (javascript) code in view.html.
* The :user should be the Github username 
* The :repo field is the name of the repository
* The :path field is the path to the Gorilla-repl worksheet in the *master* branch of the repository.

## Content

* The post needs no content apart from the headers indicating the Gorilla-repl worksheet's parameters
* Any content in the post will show up in the main blog page listing all the posts. However, only the first line is displayed.

## Implementation:

### The implementation uses 3 Selmer templates

* base.html
* view.html which extends base.html. This sheet includes the javascript required to load and display a worksheet.
* sheet.html which extends view.html. This sheet sets the source/user/repo/path in the DOM as a set of data attributes, which are read by the javascript functions in view.html, to display the worksheet.
