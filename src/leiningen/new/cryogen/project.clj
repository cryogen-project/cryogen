(defproject cryogen "0.1.0"
            :description "Simple static site generator"
            :url "https://github.com/lacarmen/cryogen"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.8.0"]
                           [ring/ring-defaults "0.2.1"]
                           [compojure "1.5.1"]
                           [cryogen-markdown "0.1.4"]
                           [cryogen-core "0.1.42"]]
            :plugins [[matsu911/lein-ring "0.9.8"]]
            :main cryogen.core
            :ring {:init cryogen.server/init
                   :auto-refresh? true
                   :refresh-paths ["src" "resources/public"]
                   :handler cryogen.server/handler})
