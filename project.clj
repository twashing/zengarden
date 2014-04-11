(defproject zengarden "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.taoensso/timbre "3.1.6"]]

  :profiles {:dev {:source-paths ["dev"]
                   :resource-paths ["." "resources/" "resources/test/"]
                   :dependencies [[midje "1.6.3"]
                                  [com.stuartsierra/component "0.2.1"]
                                  [alembic "0.2.1"]]}})
