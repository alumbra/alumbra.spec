(ns alumbra.spec.analyzed-schema
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [alumbra.spec.common]
            [alumbra.spec.type-description]))

;; ## Names

(s/def ::containing-type-name
  :alumbra/type-name)

;; ## Schema

(s/def :alumbra/analyzed-schema
  (s/keys :req-un [::types
                   ::input-types
                   ::interfaces
                   ::directives
                   ::scalars
                   ::unions
                   ::enums
                   ::schema-root
                   ::type->kind]))

;; ### Type/Kind Mapping

(s/def ::kind
  #{:type :interface :input-type :union :directive :scalar :enum})

(s/def ::type->kind
  (s/map-of :alumbra/type-name ::kind))

;; ### Fragment Spreads

(s/def ::valid-fragment-spreads
  (s/coll-of :alumbra/type-name
             :gen-max 3))

;; ### Structured Types

(s/def ::types
  (s/map-of :alumbra/type-name ::type
            :gen-max 2))

(s/def ::input-types
  (s/map-of :alumbra/type-name ::input-type
            :gen-max 1))

(s/def ::input-type
  (s/keys :req-un [:alumbra/type-name
                   ::inline-directives
                   ::implements
                   ::fields]))

(s/def ::type
  (s/keys :req-un [:alumbra/type-name
                   ::inline-directives
                   ::implements
                   ::valid-fragment-spreads
                   ::fields]))

(s/def ::implements
  (s/coll-of :alumbra/type-name
             :gen-max 3))

(s/def ::fields
  (s/map-of :alumbra/field-name ::field
            :gen-max 2))

(s/def ::field
  (s/merge
    (s/keys :req-un [:alumbra/field-name
                     ::inline-directives
                     ::containing-type-name
                     ::arguments])
    ::typed))

(s/def ::arguments
  (s/map-of :alumbra/argument-name ::argument
            :gen-max 2))

(s/def ::argument
  (s/merge
    (s/keys :req-un [:alumbra/argument-name
                     ::inline-directives]
            :opt-un [::default-value])
    ::typed))

(s/def ::typed
  ;; For validation, we only need to know whether a field is required and
  ;; what type it has, while for canonicalisation we need to know how the
  ;; value is nested.
  (s/keys :req-un [:alumbra/type-name
                   :alumbra/non-null?
                   :alumbra/type-description]))

;; ### Interfaces

(s/def ::interfaces
  (s/map-of :alumbra/type-name ::interface
            :gen-max 1))

(s/def ::interface
  (s/keys :req-un [:alumbra/type-name
                   ::inline-directives
                   ::implemented-by
                   ::valid-fragment-spreads
                   ::fields]))

(s/def ::implemented-by
  (s/coll-of :alumbra/type-name
             :gen-max 2))

;; ### Union Types

(s/def ::unions
  (s/map-of :alumbra/type-name ::union
            :gen-max 1))

(s/def ::union
  (s/keys :req-un [:alumbra/type-name
                   ::inline-directives
                   ::fields
                   ::valid-fragment-spreads
                   ::union-types]))

(s/def ::union-types
  (s/coll-of :alumbra/type-name
             :min-count 1
             :gen-max 3))

;; ### Scalars

(s/def ::scalars
  (s/map-of :alumbra/type-name ::scalar
            :gen-max 1))

(s/def ::scalar
  (s/keys :req-un [:alumbra/type-name
                   ::inline-directives]))

;; ### Directives

(s/def ::directives
  (s/map-of :alumbra/directive-name ::directive
            :gen-max 1))

(s/def ::directive
  (s/keys :req-un [:alumbra/directive-locations
                   ::arguments]))

;; ### Enums

(s/def ::enums
  (s/map-of :alumbra/type-name ::enum
            :gen-max 1))

(s/def ::enum
  (s/keys :req-un [::enum-values
                   ::inline-directives]))

(s/def ::enum-values
  (s/coll-of :alumbra/enum
             :min-count 1
             :into #{}
             :gen-max 2))

;; ### Schema Root

(s/def ::schema-root
  (s/keys :req-un [::schema-root-types
                   ::inline-directives]))

(s/def ::schema-root-types
  (s/map-of :alumbra/operation-type :alumbra/type-name
            :gen-max 1))

;; ### Inline Directives

(s/def ::inline-directives
  (s/map-of :alumbra/directive-name ::inline-directive-arguments
            :gen-max 1))

(s/def ::inline-directive-arguments
  (s/map-of :alumbra/argument-name  ::value
            :gen-max 1))

(s/def ::value
  (s/or :string   string?
        :enum     keyword?
        :integer  integer?
        :float    float?
        :boolean  boolean?
        :object   ::object
        :list     ::list
        :null     nil?))

(s/def ::object
  (s/map-of string? ::value
            :gen-max 2))

(s/def ::list
  (s/coll-of ::value
             :gen-max 2))

(s/def ::default-value
  ::value)
