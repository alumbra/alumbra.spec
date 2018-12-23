(defproject alumbra/spec "0.1.12-SNAPSHOT"
  :description "GraphQL Data Structure Specs for Clojure"
  :url "https://github.com/alumbra/alumbra.spec"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :year 2016
            :key "mit"}
  :dependencies [[org.clojure/clojure "1.10.0" :scope "provided"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-doo "0.1.11" :exclusions [org.clojure/clojure]]]
  :profiles {:dev {:dependencies [[org.clojure/clojurescript "1.10.439"
                                   :exclusions [com.google.code.findbugs/jsr305
                                                com.google.errorprone/error_prone_annotations]
                                   :scope "provided"]
                                  [org.clojure/test.check "0.9.0"]]}}
  :aliases
  {"ci" ["do"
         ["clean"]
         ["test"]
         ["doo" "node" "test" "once"]]}
  :cljsbuild
  {:builds
   [{:id "test"
     :compiler
     {:main alumbra.spec.test
      :optimizations :none
      :output-dir "target/node"
      :output-to "target/node.js"
      :parallel-build true
      :pretty-print true
      :target :nodejs
      :verbose false}
     :source-paths ["src" "test"]}]}
  :pedantic? :abort)
