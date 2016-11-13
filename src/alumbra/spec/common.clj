(ns alumbra.spec.common
  (:require [clojure.spec :as s]))

;; ## Name

(s/def :alumbra/name
  (s/and string? #(re-matches #"[_a-zA-Z][_0-9a-zA-Z]*" %)))

(s/def :alumbra/operation-name
  :alumbra/name)

(s/def :alumbra/fragment-name
  (s/and :alumbra/name #(not= % "on")))

(s/def :alumbra/field-name
  :alumbra/name)

(s/def :alumbra/field-alias
  :alumbra/name)

(s/def :alumbra/variable-name
  :alumbra/name)

(s/def :alumbra/type-name
  :alumbra/name)

(s/def :alumbra/argument-name
  :alumbra/name)

(s/def :alumbra/directive-name
  :alumbra/name)

;; ## Operation Type

(s/def :alumbra/operation-type
  #{"mutation" "query" "subscription"})

;; ## Metadata

(s/def :alumbra/metadata
  (s/keys :req-un [:alumbra/row
                   :alumbra/column]
          :opt-un [:alumbra/index]))

(s/def :alumbra/row
  (s/and integer? #(>= % 0)))

(s/def :alumbra/column
  (s/and integer? #(>= % 0)))

(s/def :alumbra/index
  (s/and integer? #(>= % 0)))

;; ## Location

(s/def :alumbra/locations
  (s/coll-of :alumbra/location
             :gen-max 1))

(s/def :alumbra/location
  :alumbra/metadata)

;; ## Flags

(s/def :alumbra/non-null?
  boolean?)

;; ## Types

(s/def :alumbra/type-class
  #{:named-type
    :list-type})

(defmulti ^:private type-class :alumbra/type-class)

(defmethod type-class :named-type
  [_]
  (s/keys :req [:alumbra/type-class
                :alumbra/type-name
                :alumbra/non-null?
                :alumbra/metadata]))

(defmethod type-class :list-type
  [_]
  (s/keys :req [:alumbra/type-class
                :alumbra/element-type
                :alumbra/non-null?
                :alumbra/metadata]))

(s/def :alumbra/type
  (s/multi-spec type-class :alumbra/type-class))

(s/def :alumbra/element-type
  :alumbra/type)

(s/def :alumbra/argument-type
  :alumbra/type)

;; ## Values

(s/def :alumbra/value-type
  #{:variable
    :integer
    :float
    :string
    :id
    :boolean
    :enum
    :object
    :list
    :null})

;; ### Literals

(s/def :alumbra/integer
  integer?)

(s/def :alumbra/float
  float?)

(s/def :alumbra/string
  string?)

(s/def :alumbra/boolean
  boolean?)

(s/def :alumbra/enum
  :alumbra/name)

(s/def :alumbra/id
  :alumbra/string)

;; ## Composite Values

(s/def :alumbra/list
  (s/coll-of :alumbra/value
             :gen-max 1))

(s/def :alumbra/object
  (s/coll-of :alumbra/object-field
             :gen-max 1))

(s/def :alumbra/object-field
  (s/keys :req [:alumbra/value
                :alumbra/field-name
                :alumbra/metadata]))

;; ### Dispatch

(defmulti graphql-value-data :alumbra/value-type)

(defmethod graphql-value-data :variable
  [_]
  (s/keys :req [:alumbra/value-type
                :alumbra/variable-name]))

(defmethod graphql-value-data :integer
  [_]
  (s/keys :req [:alumbra/value-type
                :alumbra/integer]))

(defmethod graphql-value-data :float
  [_]
  (s/keys :req [:alumbra/value-type
                :alumbra/float]))

(defmethod graphql-value-data :string
  [_]
  (s/keys :req [:alumbra/value-type
                :alumbra/string]))

(defmethod graphql-value-data :boolean
  [_]
  (s/keys :req [:alumbra/value-type
                :alumbra/boolean]))

(defmethod graphql-value-data :enum
  [_]
  (s/keys :req [:alumbra/value-type
                :alumbra/enum]))

(defmethod graphql-value-data :object
  [_]
  (s/keys :req [:alumbra/value-type
                :alumbra/object]))

(defmethod graphql-value-data :list
  [_]
  (s/keys :req [:alumbra/value-type
                :alumbra/list]))

(defmethod graphql-value-data :id
  [_]
  (s/keys :req [:alumbra/value-type
                :alumbra/id]))

(defmethod graphql-value-data :null
  [_]
  (s/keys :req [:alumbra/value-type]))

(s/def :alumbra/value
  (s/merge
    (s/multi-spec graphql-value-data :alumbra/value-type)
    (s/keys :req [:alumbra/metadata])))

(s/def :alumbra/constant
  (s/and :alumbra/value #(not= (:alumbra/value-type %) :variable)))

(s/def :alumbra/scalar-value
  (s/and :alumbra/value
         (comp #{:integer :string :boolean :enum :float :id :null}
               :alumbra/value-type)))

(s/def :alumbra/argument-value
  :alumbra/value)

(s/def :alumbra/argument-default-value
  :alumbra/scalar-value)

(s/def :alumbra/default-value
  :alumbra/constant)

;; ## Directive Locations

(s/def :alumbra/directive-locations
  (s/coll-of :alumbra/directive-location
             ;; TODO :min-count 1
             :gen-max 2))

(s/def :alumbra/directive-location
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

;; ## Generic Errors

(s/def :alumbra/errors
  (s/coll-of :alumbra/error
             :min-count 1))

(s/def :alumbra/error
  (s/keys :req-un [:alumbra/error-message]
          :opt-un [:alumbra/locations]))

(s/def :alumbra/error-message
  string?)
