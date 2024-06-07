(defproject cryogen/lein-template "0.7.5"
  :description "A Leiningen template for the Cryogen static site generator"
  :url "https://github.com/cryogen-project/cryogen"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :scm {:name "git"
        :url "https://github.com/cryogen-project/cryogen.git"}
  :dependencies [[org.clojure/core.unify "0.5.7"]
                 [org.clojure/core.contracts "0.0.6"]
                 [leinjacker "0.4.3"
                  :exclusions [org.clojure/clojure
                               org.clojure/core.contracts
                               org.clojure/core.unify]]
                 [org.clojure/tools.namespace "1.1.0"
                  :exclusions [org.clojure/clojure]]]
  :eval-in-leiningen true)
