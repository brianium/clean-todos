(defproject todos "0.1.0-SNAPSHOT"
  :description "A simple clean architecture example in Clojure"
  
  :url "https://github.com/brianium/clean-todos"
  
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.854"]
                 [org.clojure/core.async "0.3.443"]
                 [yoose "0.1.1"]
                 [org.clojure/java.jdbc "0.7.0"]
                 [clj-time "0.14.0"]
                 [honeysql "0.9.0"]
                 [org.xerial/sqlite-jdbc "3.19.3"]
                 [mount "0.1.11"]]

  :source-paths ["src"]

  :target-path "target/%s/"

  :clean-targets ^{:protect false} [:target-path "resources/public/js"]

  :aliases {"api" ["with-profile" "api" "ring" "server"]
            "web" ["with-profile" "web" "figwheel"]}
  
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.10.0-alpha2"]
                                  [com.cemerick/pomegranate "0.3.1"]]
                   
                   :source-paths ["src" "test"]
                   
                   :plugins [[venantius/ultra "0.5.1"]
                             [lein-kibit "0.1.6-beta2"]]}

             :cli {:dependencies [[org.clojure/tools.cli "0.3.5"]
                                  [io.aviso/pretty "0.1.34"]]

                   :plugins [[lein-binplus "0.6.2"]]

                   :main todos.delivery.cli.core}

             
             :api {:dependencies [[ring/ring-core "1.6.2"]
                                  [ring/ring-jetty-adapter "1.6.2"]
                                  [ring/ring-json "0.4.0"]
                                  [ring/ring-defaults "0.3.1"]
                                  [ring-cors "0.1.11"]
                                  [compojure "1.6.0"]]

                   :plugins [[lein-ring "0.12.5"]]

                   :ring {:handler todos.delivery.api.core/app
                          :init    mount.core/start
                          :port    4242
                          :reload-paths ["src/todos/core" "src/todos/delivery/api"]}}

             :web {:dependencies [[reagent "0.6.2"]
                                  [re-frame "0.9.4"]
                                  [day8.re-frame/http-fx "0.1.4"]
                                  [bidi "2.1.2"]
                                  [com.cemerick/piggieback "0.2.2"]
                                  [figwheel-sidecar "0.5.12"]]

                   :plugins [[lein-figwheel "0.5.12"]
                             [lein-cljsbuild "1.1.7"]]}

             :uberjar {:aot [todos.delivery.cli.core]}}

  :cljsbuild {:builds {:web {:source-paths ["src"]
                             :figwheel     {:on-jsload "todos.delivery.web.core/main"}
                             :compiler     {:main                 "todos.delivery.web.core"
                                            :source-map-timestamp true
                                            :source-map           true
                                            :optimizations        :none
                                            :asset-path           "js"
                                            :output-dir           "resources/public/js"
                                            :output-to            "resources/public/js/todos.js"}}}}
  
  :bin {:name "todos"})
