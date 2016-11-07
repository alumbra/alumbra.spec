(ns alumbra.spec.schema
  (:require [clojure.spec :as s]
            [alumbra.spec document]))

;; ## Schema

(s/def :graphql/schema
  (s/keys :req [:graphql/metadata]
          :opt [:graphql/type-definitions
                :graphql/input-type-definitions
                :graphql/type-extensions
                :graphql/interface-definitions
                :graphql/schema-definitions
                :graphql/enum-definitions
                :graphql/scalar-definitions
                :graphql/directive-definitions
                :graphql/union-definitions]))

;; ## Type Definition/Extension

(s/def :graphql/type-definitions
  (s/coll-of :graphql/type-definition
             :gen-max 4))

(s/def :graphql/type-extensions
  (s/coll-of :graphql/type-definition
             :gen-max 2))

(s/def :graphql/type-definition
  (s/keys :req [:graphql/type-name
                :graphql/type-fields
                :graphql/metadata]
          :opt [:graphql/interface-types]))

(s/def :graphql/interface-types
  (s/coll-of :graphql/interface-type
             :min-count 1
             :gen-max 3))

(s/def :graphql/interface-type
  (s/keys :req [:graphql/type-name
                :graphql/metadata]))

(s/def :graphql/type-fields
  (s/coll-of :graphql/type-field
             :min-count 1
             :gen-max 3))

(s/def :graphql/type-field
  (s/keys :req [:graphql/field-name
                :graphql/type
                :graphql/metadata]
          :opt [:graphql/type-field-arguments]))

(s/def :graphql/type-field-arguments
  (s/coll-of :graphql/type-field-argument
             :min-count 1
             :gen-max 3))

(s/def :graphql/type-field-argument
  (s/keys :req [:graphql/argument-name
                :graphql/argument-type
                :graphql/metadata]
          :opt [:graphql/argument-default-value]))

(s/def :graphql/argument-type
  :graphql/type)

(s/def :graphql/argument-default-value
  (s/and :graphql/constant
         (comp
           #{:integer :float :boolean :string :enum}
           :graphql/value-type)))

;; ## Input Type Definition

(s/def :graphql/input-type-definitions
  (s/coll-of :graphql/input-type-definition
             :gen-max 4))

(s/def :graphql/input-type-definition
  (s/keys :req [:graphql/type-name
                :graphql/input-type-fields
                :graphql/metadata]))

(s/def :graphql/input-type-fields
  (s/coll-of :graphql/input-type-field
             :min-count 1
             :gen-max 3))

(s/def :graphql/input-type-field
  (s/keys :req [:graphql/field-name
                :graphql/type
                :graphql/metadata]))

;; ## Interface Definition

(s/def :graphql/interface-definitions
  (s/coll-of :graphql/interface-definition
             :gen-max 2))

(s/def :graphql/interface-definition
  (s/keys :req [:graphql/type-name
                :graphql/type-fields
                :graphql/metadata]))

;; ## Scalar Definition

(s/def :graphql/scalar-definitions
  (s/coll-of :graphql/scalar-definition
             :gen-max 2))

(s/def :graphql/scalar-definition
  (s/keys :req [:graphql/type-name
                :graphql/metadata]))

;; ## Directive Definition

(s/def :graphql/directive-definitions
  (s/coll-of :graphql/directive-definition
             :gen-max 1))

(s/def :graphql/directive-definition
  (s/keys :req [:graphql/directive-name
                :graphql/type-condition
                :graphql/metadata]))

;; ## Union Definition

(s/def :graphql/union-definitions
  (s/coll-of :graphql/union-definition
             :gen-max 2))

(s/def :graphql/union-definition
  (s/keys :req [:graphql/type-name
                :graphql/union-types
                :graphql/metadata]))

(s/def :graphql/union-types
  (s/coll-of :graphql/union-type
             :min-count 1
             :gen-max 3))

(s/def :graphql/union-type
  (s/keys :req [:graphql/type-name
                :graphql/metadata]))

;; ## Enum Definition

(s/def :graphql/enum-definitions
  (s/coll-of :graphql/enum-definition
             :gen-max 2))

(s/def :graphql/enum-definition
  (s/keys :req [:graphql/type-name
                :graphql/enum-fields
                :graphql/metadata]))

(s/def :graphql/enum-fields
  (s/coll-of :graphql/enum-field
             :min-count 1
             :gen-max 3))

(s/def :graphql/enum-field
  (s/keys :req [:graphql/enum
                :graphql/metadata]
          :opt [:graphql/integer]))

;; ## Schema Definition

(s/def :graphql/schema-definitions
  (s/coll-of :graphql/schema-definition
             :gen-max 1))

(s/def :graphql/schema-definition
  (s/keys :req [:graphql/schema-fields
                :graphql/metadata]))

(s/def :graphql/schema-fields
  (s/coll-of :graphql/schema-field
             :min-count 1
             :gen-max 3))

(s/def :graphql/schema-field
  (s/keys :req [:graphql/operation-type
                :graphql/schema-type
                :graphql/metadata]))

(s/def :graphql/schema-type
  (s/keys :req [:graphql/type-name
                :graphql/metadata]))
