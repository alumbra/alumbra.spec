(ns alumbra.spec.analyzed-schema
  (:require [clojure.spec :as s]
            [clojure.spec.gen :as gen]
            [alumbra.spec common]))

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
                   ::implements
                   ::fields]))

(s/def ::type
  (s/keys :req-un [:alumbra/type-name
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
                     ::containing-type-name
                     ::arguments])
    ::typed))

(s/def ::arguments
  (s/map-of :alumbra/argument-name ::argument
            :gen-max 2))

(s/def ::argument
  (s/merge
    (s/keys :req-un [:alumbra/argument-name])
    ::typed))

(s/def ::typed
  ;; For validation, we only need to know whether a field is required and
  ;; what type it has, while for canonicalisation we need to know how the
  ;; value is nested.
  (s/keys :req-un [:alumbra/type-name
                   :alumbra/non-null?
                   ::type-description]))

(s/def ::type-description
  (s/with-gen
    (s/or :unnested ::unnested
          :nested   ::nested)
    #(gen/frequency
       [[9 (s/gen ::unnested)]
        [1 (gen/bind (gen/return nil) (fn [_] (s/gen ::nested)))]])))

(s/def ::nested
  (s/keys :req-un [:alumbra/non-null?
                   ::type-description]))

(s/def ::unnested
  (s/keys :req-un [:alumbra/non-null?
                   :alumbra/type-name]))

;; ### Interfaces

(s/def ::interfaces
  (s/map-of :alumbra/type-name ::interface
            :gen-max 1))

(s/def ::interface
  (s/keys :req-un [:alumbra/type-name
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
                   ::fields
                   ::valid-fragment-spreads
                   ::union-types]))

(s/def ::union-types
  (s/coll-of :alumbra/type-name
             :min-count 1
             :gen-max 3))

;; ### Scalars

(s/def ::scalars
  (s/coll-of :alumbra/type-name
             :gen-max 1))

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
  (s/coll-of :alumbra/enum
             :min-count 1
             :into #{}
             :gen-max 2))

;; ### Schema Root

(s/def ::schema-root
  (s/map-of :alumbra/operation-type :alumbra/type-name
            :gen-max 1))
