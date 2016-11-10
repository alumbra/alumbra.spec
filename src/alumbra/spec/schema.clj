(ns alumbra.spec.schema
  (:require [clojure.spec :as s]
            [alumbra.spec
             [common :as common]
             [document :as document]]))

(common/import-specs
  ::metadata
  ::argument-name
  ::directive-name
  ::field-name
  ::type-name
  ::variable-name
  ::non-null?
  ::operation-type)

;; ## Schema

(s/def ::schema
  (s/keys :req [::metadata]
          :opt [::type-definitions
                ::input-type-definitions
                ::type-extensions
                ::interface-definitions
                ::schema-definitions
                ::enum-definitions
                ::scalar-definitions
                ::directive-definitions
                ::union-definitions]))

;; ## Type Definition/Extension

(s/def ::type-definitions
  (s/coll-of ::type-definition
             :gen-max 4))

(s/def ::type-extensions
  (s/coll-of ::type-definition
             :gen-max 2))

(s/def ::type-definition
  (s/keys :req [::type-fields
                ::type-name
                ::metadata]
          :opt [::interface-types]))

(s/def ::interface-types
  (s/coll-of ::interface-type
             :min-count 1
             :gen-max 3))

(s/def ::interface-type
  (s/keys :req [::type-name
                ::metadata]))

(s/def ::type-fields
  (s/coll-of ::type-field
             :min-count 1
             :gen-max 3))

(s/def ::type-field
  (s/keys :req [::field-name
                ::type
                ::metadata]
          :opt [::arguments]))

(s/def ::arguments
  (s/coll-of ::argument
             :min-count 1
             :gen-max 3))

(s/def ::argument
  (s/keys :req [::argument-name
                ::argument-type
                ::metadata]
          :opt [::default-value]))

;; ## Input Type Definition

(s/def ::input-type-definitions
  (s/coll-of ::input-type-definition
             :gen-max 4))

(s/def ::input-type-definition
  (s/keys :req [::input-type-fields
                ::type-name
                ::metadata]))

(s/def ::input-type-fields
  (s/coll-of ::input-type-field
             :min-count 1
             :gen-max 3))

(s/def ::input-type-field
  (s/keys :req [::field-name
                ::type
                ::metadata]))

;; ## Interface Definition

(s/def ::interface-definitions
  (s/coll-of ::interface-definition
             :gen-max 2))

(s/def ::interface-definition
  (s/keys :req [::type-fields
                ::type-name
                ::metadata]))

;; ## Scalar Definition

(s/def ::scalar-definitions
  (s/coll-of ::scalar-definition
             :gen-max 2))

(s/def ::scalar-definition
  (s/keys :req [::type-name
                ::metadata]))

;; ## Directive Definition

(s/def ::directive-definitions
  (s/coll-of ::directive-definition
             :gen-max 1))

(s/def ::directive-definition
  (s/keys :req [::directive-locations
                ::directive-name
                ::metadata]
          :opt [::arguments]))

(s/def ::directive-locations
  (s/coll-of ::directive-location
             ;; TODO :min-count 1
             :gen-max 2))

(s/def ::directive-location
  #{:query
    :mutation
    :subscription
    :field
    :fragment-definition
    :fragment-spread
    :inline-fragment
    :schema
    :scalar
    :object
    :field-definition
    :argument-definition
    :interface
    :union
    :enum
    :enum-value
    :input-object
    :input-field-definition})

;; ## Union Definition

(s/def ::union-definitions
  (s/coll-of ::union-definition
             :gen-max 2))

(s/def ::union-definition
  (s/keys :req [::union-types
                ::type-name
                ::metadata]))

(s/def ::union-types
  (s/coll-of ::union-type
             :min-count 1
             :gen-max 3))

(s/def ::union-type
  (s/keys :req [::type-name
                ::metadata]))

;; ## Enum Definition

(s/def ::enum-definitions
  (s/coll-of ::enum-definition
             :gen-max 2))

(s/def ::enum-definition
  (s/keys :req [::enum-fields
                ::type-name
                ::metadata]))

(s/def ::enum-fields
  (s/coll-of ::enum-field
             :min-count 1
             :gen-max 3))

(s/def ::enum-field
  (s/keys :req [::enum
                ::metadata]
          :opt [::integer]))

;; ## Schema Definition

(s/def ::schema-definitions
  (s/coll-of ::schema-definition
             :gen-max 1))

(s/def ::schema-definition
  (s/keys :req [::schema-fields
                ::metadata]))

(s/def ::schema-fields
  (s/coll-of ::schema-field
             :min-count 1
             :gen-max 3))

(s/def ::schema-field
  (s/keys :req [::schema-type
                ::operation-type
                ::metadata]))

(s/def ::schema-type
  (s/keys :req [::type-name
                ::metadata]))

;; ## Value

(s/def ::value-type
  #{:integer :float :string :boolean :enum})

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

;; ### Dispatch

(defmulti graphql-value-data ::value-type)

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

(s/def ::default-value
  (s/merge
    (s/multi-spec graphql-value-data ::value-type)
    (s/keys :req [::metadata])))

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
