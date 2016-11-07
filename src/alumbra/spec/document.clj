(ns alumbra.spec.document
  (:require [clojure.spec :as s]))

;; ## Name

(s/def :graphql/name
  (s/and string? #(re-matches #"[_a-zA-Z][_0-9a-zA-Z]*" %)))

;; ## Metadata

(s/def :graphql/metadata
  (s/keys :req-un [:graphql/row
                   :graphql/column
                   :graphql/index]))

(s/def :graphql/row
  (s/and integer? #(>= % 0)))

(s/def :graphql/column
  (s/and integer? #(>= % 0)))

(s/def :graphql/index
  (s/and integer? #(>= % 0)))

;; ## Document

(s/def :graphql/document
  (s/keys :opt [:graphql/operations
                :graphql/fragments]))

;; ### Operation

(s/def :graphql/operations
  (s/coll-of :graphql/operation
             :gen-max 5))

(s/def :graphql/operation
  (s/keys :req [:graphql/selection-set
                :graphql/operation-type
                :graphql/metadata]
          :opt [:graphql/operation-name
                :graphql/variables
                :graphql/directives]))

(s/def :graphql/operation-name
  :graphql/name)

(s/def :graphql/operation-type
  #{"mutation" "query" "subscription"})

;; ### Fragments

(s/def :graphql/fragments
  (s/coll-of :graphql/fragment
             :gen-max 5))

(s/def :graphql/fragment
  (s/keys :req [:graphql/fragment-name
                :graphql/type-condition
                :graphql/selection-set
                :graphql/metadata]
          :opt [:graphql/directives]))

(s/def :graphql/fragment-name
  (s/and :graphql/name #(not= % "on")))

(s/def :graphql/type-condition
  (s/keys :req [:graphql/type-name
                :graphql/metadata]))

;; ### Selection Set

(s/def :graphql/selection-set
  (s/coll-of :graphql/selection
             :min-count 1
             :gen-max 5))

(s/def :graphql/selection
  (s/or :field           :graphql/field
        :fragment-spread :graphql/fragment-spread
        :inline-fragment :graphql/inline-fragment))

(s/def :graphql/field
  (s/keys :req [:graphql/field-name
                :graphql/metadata]
          :opt [:graphql/field-alias
                :graphql/arguments
                :graphql/directives
                :graphql/selection-set]))

(s/def :graphql/field-name
  :graphql/name)

(s/def :graphql/field-alias
  :graphql/name)

(s/def :graphql/fragment-spread
  (s/keys :req [:graphql/fragment-name
                :graphql/metadata]
          :opt [:graphql/directives]))

(s/def :graphql/inline-fragment
  (s/keys :req [:graphql/selection-set
                :graphql/metadata]
          :opt [:graphql/directives
                :graphql/type-condition]))

;; ## Values

;; ### Literals

(s/def :graphql/integer
  integer?)

(s/def :graphql/float
  float?)

(s/def :graphql/string
  string?)

(s/def :graphql/boolean
  boolean?)

(s/def :graphql/enum
  :graphql/name)

(s/def :graphql/list
  (s/coll-of :graphql/value
             :gen-max 3))

(s/def :graphql/object
  (s/coll-of :graphql/object-field
             :gen-max 3))

(s/def :graphql/object-field
  (s/keys :req [:graphql/field-name
                :graphql/value
                :graphql/metadata]))

;; ### Dispatch

(s/def :graphql/value-type
  #{:variable :integer :float :string
    :boolean :enum :object :list})

(defmulti ^:private graphql-value-data :graphql/value-type)

(defmethod graphql-value-data :variable
  [_]
  (s/keys :req [:graphql/value-type
                :graphql/variable
                :graphql/metadata]))

(defmethod graphql-value-data :integer
  [_]
  (s/keys :req [:graphql/value-type
                :graphql/integer
                :graphql/metadata]))

(defmethod graphql-value-data :float
  [_]
  (s/keys :req [:graphql/value-type
                :graphql/float
                :graphql/metadata]))

(defmethod graphql-value-data :string
  [_]
  (s/keys :req [:graphql/value-type
                :graphql/string
                :graphql/metadata]))

(defmethod graphql-value-data :boolean
  [_]
  (s/keys :req [:graphql/value-type
                :graphql/boolean
                :graphql/metadata]))

(defmethod graphql-value-data :enum
  [_]
  (s/keys :req [:graphql/value-type
                :graphql/enum
                :graphql/metadata]))

(defmethod graphql-value-data :object
  [_]
  (s/keys :req [:graphql/value-type
                :graphql/object
                :graphql/metadata]))

(defmethod graphql-value-data :list
  [_]
  (s/keys :req [:graphql/value-type
                :graphql/list
                :graphql/metadata]))

(s/def :graphql/value
  (s/multi-spec graphql-value-data :graphql/value-type))

(s/def :graphql/constant
 (s/and :graphql/value #(not= (:graphql/value-type %) :variable)))

;; ## Variables

(s/def :graphql/variable-name
  :graphql/name)

(s/def :graphql/variable
  (s/keys :req [:graphql/variable-name
                :graphql/metadata]))

(s/def :graphql/variable-definition
  (s/keys :req [:graphql/variable-name
                :graphql/type
                :graphql/metadata]
          :opt [:graphql/default-value]))

(s/def :graphql/default-value
  :graphql/constant)

(s/def :graphql/variables
  (s/coll-of :graphql/variable-definition
             :min-count 1
             :gen-max 3))

;; ## Types

(s/def :graphql/type-class
  #{:named-type
    :list-type})

(defmulti ^:private type-class :graphql/type-class)

(defmethod type-class :named-type
  [_]
  (s/keys :req [:graphql/type-class
                :graphql/type-name
                :graphql/non-null?
                :graphql/metadata]))

(defmethod type-class :list-type
  [_]
  (s/keys :req [:graphql/type-class
                :graphql/element-type
                :graphql/non-null?
                :graphql/metadata]))

(s/def :graphql/type
  (s/multi-spec type-class :graphql/type-class))

(s/def :graphql/element-type
  :graphql/type)

(s/def :graphql/type-name
  :graphql/name)

(s/def :graphql/non-null?
  :graphql/boolean)

;; ## Arguments

(s/def :graphql/arguments
  (s/coll-of :graphql/argument
             :min-count 1
             :gen-max 3))

(s/def :graphql/argument
  (s/keys :req [:graphql/argument-name
                :graphql/argument-value
                :graphql/metadata]))

(s/def :graphql/argument-name
  :graphql/name)

(s/def :graphql/argument-value
  :graphql/value)

;; ## Directives

(s/def :graphql/directives
  (s/coll-of :graphql/directive
             :min-count 1
             :gen-max 2))

(s/def :graphql/directive
  (s/keys :req [:graphql/directive-name
                :graphql/metadata]
          :opt [:graphql/arguments]))

(s/def :graphql/directive-name
  :graphql/name)
