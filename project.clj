(defproject zengarden "0.1.1"
  :description "Zengarden is a library for rendering CSS in Clojure."
  :url "https://github.com/twashing/zengarden"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [prismatic/schema "0.2.1"]
                 [com.taoensso/timbre "3.1.6"]]

  :profiles {:dev {:source-paths ["dev"]
                   :resource-paths ["." "resources/" "resources/test/"]
                   :dependencies [[midje "1.6.3"]
                                  [com.stuartsierra/component "0.2.1"]
                                  [alembic "0.2.1"]]}}

  :lein-release {:deploy-via :clojars})
