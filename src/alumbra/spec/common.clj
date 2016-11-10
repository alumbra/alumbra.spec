(ns alumbra.spec.common
  (:require [clojure.spec :as s]))

;; ## Name

(s/def ::name
  (s/and string? #(re-matches #"[_a-zA-Z][_0-9a-zA-Z]*" %)))

(s/def ::operation-name
  ::name)

(s/def ::fragment-name
  (s/and ::name #(not= % "on")))

(s/def ::field-name
  ::name)

(s/def ::field-alias
  ::name)

(s/def ::variable-name
  ::name)

(s/def ::type-name
  ::name)

(s/def ::argument-name
  ::name)

(s/def ::directive-name
  ::name)

;; ## Operation Type

(s/def ::operation-type
  #{"mutation" "query" "subscription"})

;; ## Metadata

(s/def ::metadata
  (s/keys :req-un [::row
                   ::column
                   ::index]))

(s/def ::row
  (s/and integer? #(>= % 0)))

(s/def ::column
  (s/and integer? #(>= % 0)))

(s/def ::index
  (s/and integer? #(>= % 0)))

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
  ::name)

(s/def ::variable
  (s/keys :req [::variable-name
                ::metadata]))

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

(defmulti ^:private graphql-value-data ::value-type)

(defmethod graphql-value-data :variable
  [_]
  (s/keys :req [::value-type
                ::variable
                ::metadata]))

(defmethod graphql-value-data :integer
  [_]
  (s/keys :req [::value-type
                ::integer
                ::metadata]))

(defmethod graphql-value-data :float
  [_]
  (s/keys :req [::value-type
                ::float
                ::metadata]))

(defmethod graphql-value-data :string
  [_]
  (s/keys :req [::value-type
                ::string
                ::metadata]))

(defmethod graphql-value-data :boolean
  [_]
  (s/keys :req [::value-type
                ::boolean
                ::metadata]))

(defmethod graphql-value-data :enum
  [_]
  (s/keys :req [::value-type
                ::enum
                ::metadata]))

(defmethod graphql-value-data :object
  [_]
  (s/keys :req [::value-type
                ::object
                ::metadata]))

(defmethod graphql-value-data :list
  [_]
  (s/keys :req [::value-type
                ::list
                ::metadata]))

(s/def ::value
  (s/multi-spec graphql-value-data ::value-type))

(s/def ::constant
 (s/and ::value #(not= (::value-type %) :variable)))

(s/def ::argument-value
  ::value)

(s/def ::default-value
  ::constant)

(s/def ::argument-default-value
  (s/and ::value
         (comp
           #{:integer :float :boolean :string :enum}
           ::value-type)))

;; ## Flags

(s/def ::non-null?
  ::boolean)

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

;; ## Alias Macro

(defmacro import-specs
  [& ks]
  `(do
     ~@(for [k ks]
         `(s/def ~k
            ~(keyword "alumbra.spec.common" (name k))))))
