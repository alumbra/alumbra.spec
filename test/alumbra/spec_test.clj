(ns alumbra.spec-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            alumbra.spec))

(deftest t-analyzer-spec
  (is (= 100 (count (s/exercise :alumbra/analyzed-schema 100)))))

(deftest t-document-spec
  (is (= 100 (count (s/exercise :alumbra/document 100)))))

(deftest t-schema-spec
  (is (= 100 (count (s/exercise :alumbra/schema 100)))))

(deftest t-validation-errors-spec
  (is (= 100 (count (s/exercise :alumbra/validation-errors 100)))))

(deftest t-parser-errors-spec
  (is (= 100 (count (s/exercise :alumbra/parser-errors 100)))))

(deftest t-canonical-operation-spec
  (is (= 100 (count (s/exercise :alumbra/canonical-operation 100)))))
