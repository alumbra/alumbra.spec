(defproject alumbra/spec "0.1.7-SNAPSHOT"
  :description "GraphQL Data Structure Specs for Clojure"
  :url "https://github.com/alumbra/alumbra.spec"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :year 2016
            :key "mit"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14" :scope "provided"]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}}
  :pedantic? :abort)
