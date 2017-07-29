(defproject todos "0.1.0-SNAPSHOT"
  :description "A simple clean architecture example in Clojure"
  
  :url "https://github.com/brianium/clean-todos"
  
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/core.async "0.3.443"]
                 [org.clojure/java.jdbc "0.7.0"]
                 [clj-time "0.14.0"]
                 [honeysql "0.9.0"]]

  :source-paths ["src" "test"]
  
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.10.0-alpha2"]]}

             :cli {:dependencies [[org.clojure/tools.cli "0.3.5"]
                                  [io.aviso/pretty "0.1.34"]
                                  [mount "0.1.11"]
                                  [org.xerial/sqlite-jdbc "3.19.3"]]

                   :main todos.delivery.cli.core}})
