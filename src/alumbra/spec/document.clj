(ns alumbra.spec.document
  (:require [clojure.spec :as s]
            [alumbra.spec.common :as common]))

(common/import-specs
  ::metadata
  ::operation-name
  ::operation-type
  ::fragment-name
  ::type-name
  ::field-name
  ::field-alias
  ::argument-name
  ::argument-value
  ::directive-name
  ::variable-name
  ::default-value
  ::type)

;; ## Document

(s/def ::document
  (s/keys :opt [::operations
                ::fragments]))

;; ### Operation

(s/def ::operations
  (s/coll-of ::operation
             :gen-max 1))

(s/def ::operation
  (s/keys :req [::selection-set
                ::operation-type
                ::metadata]
          :opt [::operation-name
                ::variables
                ::directives]))

;; ### Fragments

(s/def ::fragments
  (s/coll-of ::fragment
             :gen-max 1))

(s/def ::fragment
  (s/keys :req [::fragment-name
                ::type-condition
                ::selection-set
                ::metadata]
          :opt [::directives]))

(s/def ::type-condition
  (s/keys :req [::type-name
                ::metadata]))

;; ### Selection Set

(s/def ::selection-set
  (s/coll-of ::selection
             :min-count 1
             :gen-max 1))

(s/def ::selection
  (s/or :field           ::field
        :fragment-spread ::fragment-spread
        :inline-fragment ::inline-fragment))

(s/def ::field
  (s/keys :req [::field-name
                ::metadata]
          :opt [::field-alias
                ::arguments
                ::directives
                ::selection-set]))

(s/def ::fragment-spread
  (s/keys :req [::fragment-name
                ::metadata]
          :opt [::directives]))

(s/def ::inline-fragment
  (s/keys :req [::selection-set
                ::metadata]
          :opt [::directives
                ::type-condition]))

;; ## Variables

(s/def ::variable-definition
  (s/keys :req [::type
                ::variable-name
                ::metadata]
          :opt [::default-value]))

(s/def ::variables
  (s/coll-of ::variable-definition
             :min-count 1
             :gen-max 1))

;; ## Arguments

(s/def ::arguments
  (s/coll-of ::argument
             :min-count 1
             :gen-max 1))

(s/def ::argument
  (s/keys :req [::argument-name
                ::metadata
                ::argument-value]))

;; ## Directives

(s/def ::directives
  (s/coll-of ::directive
             :min-count 1
             :gen-max 1))

(s/def ::directive
  (s/keys :req [::directive-name
                ::metadata]
          :opt [::arguments]))
