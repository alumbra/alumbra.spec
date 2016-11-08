(ns alumbra.spec-test
  (:require [clojure.test :refer :all]
            [clojure.spec :as s]
            [alumbra spec]))

(deftest t-document-spec
  (is (= 100 (count (s/exercise :graphql/document 100)))))

(deftest t-schema-spec
  (is (= 100 (count (s/exercise :graphql/schema 100)))))

(deftest t-validator-spec
  (is (= 100 (count (s/exercise :validator/errors 100)))))

(deftest t-canonical-document-spec
  (is (= 100 (count (s/exercise :graphql/canonical-document 100)))))
