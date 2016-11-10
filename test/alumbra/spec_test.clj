(ns alumbra.spec-test
  (:require [clojure.test :refer :all]
            [clojure.spec :as s]
            [alumbra.spec
             [document :as document]
             [schema :as schema]
             [canonical-document :as canonical]
             [validator :as validator]]))

(deftest t-document-spec
  (is (= 100 (count (s/exercise ::document/document 100)))))

(deftest t-schema-spec
  (is (= 100 (count (s/exercise ::schema/schema 100)))))

(deftest t-validator-spec
  (is (= 100 (count (s/exercise ::validator/errors 100)))))

(deftest t-canonical-document-spec
  (is (= 100 (count (s/exercise ::canonical/document 100)))))
