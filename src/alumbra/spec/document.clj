(ns alumbra.spec.document
  (:require [clojure.spec :as s]
            [alumbra.spec common]))

;; ## Document

(s/def :alumbra/document
  (s/keys :opt [:alumbra/operations
                :alumbra/fragments]))

;; ### Operation

(s/def :alumbra/operations
  (s/coll-of :alumbra/operation
             :gen-max 1))

(s/def :alumbra/operation
  (s/keys :req [:alumbra/selection-set
                :alumbra/operation-type
                :alumbra/metadata]
          :opt [:alumbra/operation-name
                :alumbra/variables
                :alumbra/directives]))

;; ### Fragments

(s/def :alumbra/fragments
  (s/coll-of :alumbra/fragment
             :gen-max 1))

(s/def :alumbra/fragment
  (s/keys :req [:alumbra/fragment-name
                :alumbra/type-condition
                :alumbra/selection-set
                :alumbra/metadata]
          :opt [:alumbra/directives]))

(s/def :alumbra/type-condition
  (s/keys :req [:alumbra/type-name
                :alumbra/metadata]))

;; ### Selection Set

(s/def :alumbra/selection-set
  (s/coll-of :alumbra/selection
             :min-count 1
             :gen-max 1))

(s/def :alumbra/selection
  (s/or :field           :alumbra/field
        :fragment-spread :alumbra/fragment-spread
        :inline-fragment :alumbra/inline-fragment))

(s/def :alumbra/field
  (s/keys :req [:alumbra/field-name
                :alumbra/metadata]
          :opt [:alumbra/field-alias
                :alumbra/arguments
                :alumbra/directives
                :alumbra/selection-set]))

(s/def :alumbra/fragment-spread
  (s/keys :req [:alumbra/fragment-name
                :alumbra/metadata]
          :opt [:alumbra/directives]))

(s/def :alumbra/inline-fragment
  (s/keys :req [:alumbra/selection-set
                :alumbra/metadata]
          :opt [:alumbra/directives
                :alumbra/type-condition]))

;; ## Variables

(s/def :alumbra/variable-definition
  (s/keys :req [:alumbra/type
                :alumbra/variable-name
                :alumbra/metadata]
          :opt [:alumbra/default-value]))

(s/def :alumbra/variables
  (s/coll-of :alumbra/variable-definition
             :min-count 1
             :gen-max 1))

;; ## Arguments

(s/def :alumbra/arguments
  (s/coll-of :alumbra/argument
             :min-count 1
             :gen-max 1))

(s/def :alumbra/argument
  (s/keys :req [:alumbra/argument-name
                :alumbra/metadata
                :alumbra/argument-value]))

;; ## Directives

(s/def :alumbra/directives
  (s/coll-of :alumbra/directive
             :min-count 1
             :gen-max 1))

(s/def :alumbra/directive
  (s/keys :req [:alumbra/directive-name
                :alumbra/metadata]
          :opt [:alumbra/arguments]))
