(defproject cryogen "0.1.0"
  :description "A blog about Lisp, life and sometimes Scala"
  :url "http://www.evalapply.io"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-devel "1.4.0"]
                 [compojure "1.5.0"]
                 [ring-server "0.4.0"]
                 [cryogen-markdown "0.1.2"]
                 [cryogen-core "0.1.39"]]
  :plugins [[lein-ring "0.9.7"]]
  :main cryogen.core
  :ring {:init cryogen.server/init
         :handler cryogen.server/handler})
