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
  ::non-null?
  ::directive-name
  ::variable-name)

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

;; ## Values

(s/def ::value-type
  #{:variable :integer :float :string
    :boolean :enum :object :list})

;; ### Literals

(s/def ::integer
  integer?)

(s/def ::float
  float?)

(s/def ::string
  string?)

(s/def ::boolean
  boolean?)

(s/def ::enum
  ::common/name)

;; ## Composite Values

(s/def ::list
  (s/coll-of ::value
             :gen-max 1))

(s/def ::object
  (s/coll-of ::object-field
             :gen-max 1))

(s/def ::object-field
  (s/keys :req [::value
                ::field-name
                ::metadata]))

;; ### Dispatch

(defmulti graphql-value-data ::value-type)

(defmethod graphql-value-data :variable
  [_]
  (s/keys :req [::value-type
                ::variable-name]))

(defmethod graphql-value-data :integer
  [_]
  (s/keys :req [::value-type
                ::integer]))

(defmethod graphql-value-data :float
  [_]
  (s/keys :req [::value-type
                ::float]))

(defmethod graphql-value-data :string
  [_]
  (s/keys :req [::value-type
                ::string]))

(defmethod graphql-value-data :boolean
  [_]
  (s/keys :req [::value-type
                ::boolean]))

(defmethod graphql-value-data :enum
  [_]
  (s/keys :req [::value-type
                ::enum]))

(defmethod graphql-value-data :object
  [_]
  (s/keys :req [::value-type
                ::object]))

(defmethod graphql-value-data :list
  [_]
  (s/keys :req [::value-type
                ::list]))

(s/def ::value
  (s/merge
    (s/multi-spec graphql-value-data ::value-type)
    (s/keys :req [::metadata])))

(s/def ::constant
  (s/and ::value #(not= (::value-type %) :variable)))

(s/def ::argument-value
  ::value)

(s/def ::default-value
  ::constant)

;; ## Types

(s/def ::type-class
  #{:named-type
    :list-type})

(defmulti ^:private type-class ::type-class)

(defmethod type-class :named-type
  [_]
  (s/keys :req [::type-class
                ::type-name
                ::non-null?
                ::metadata]))

(defmethod type-class :list-type
  [_]
  (s/keys :req [::type-class
                ::element-type
                ::non-null?
                ::metadata]))

(s/def ::type
  (s/multi-spec type-class ::type-class))

(s/def ::element-type
  ::type)

(s/def ::argument-type
  ::type)
