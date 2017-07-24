(defproject todos/app "0.1.0-SNAPSHOT"
  :description "A simple clean architecture example in Clojure. This is the main parent project.clj
                to be referenced by delivery apps"
  
  :url "https://github.com/brianium/clean-todos"
  
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/core.async "0.3.443"]]

  :source-paths ["app/src"]

  :test-paths ["app/test"]

  :profiles {:dev {:source-paths ["app/src" "app/test"]
                   :dependencies [[org.clojure/test.check "0.10.0-alpha2"]]}})
