(ns alumbra.spec.analyzer
  (:require [clojure.spec :as s]
            [clojure.spec.gen :as gen]
            [alumbra.spec.common :as common]))

(common/import-specs
  ::type-name
  ::argument-name
  ::field-name
  ::directive-name
  ::non-null?
  ::operation-type)

(s/def ::containing-type-name
  ::type-name)

;; ## Schema

(s/def ::schema
  (s/keys :req [::types
                ::input-types
                ::interfaces
                ::directives
                ::schema-root
                ::scalars
                ::unions
                ::type->kind]))

;; ### Type/Kind Mapping

(s/def ::kind
  #{:type :interface :input-type :union :directive :scalar :enum})

(s/def ::type->kind
  (s/map-of ::type-name ::kind))

;; ### Fragment Spreads

(s/def ::valid-fragment-spreads
  (s/coll-of ::type-name
             :gen-max 3))

;; ### Structured Types

(s/def ::types
  (s/map-of ::type-name ::type
            :gen-max 2))

(s/def ::input-types
  (s/map-of ::type-name ::input-type
            :gen-max 1))

(s/def ::input-type
  (s/keys :req [::type-name
                ::implements
                ::fields]))

(s/def ::type
  (s/keys :req [::type-name
                ::implements
                ::valid-fragment-spreads
                ::fields]))

(s/def ::implements
  (s/coll-of ::type-name
             :gen-max 3))

(s/def ::fields
  (s/map-of ::field-name ::field
            :gen-max 2))

(s/def ::field
  (s/merge
    (s/keys :req [::field-name
                  ::containing-type-name
                  ::arguments])
    ::typed))

(s/def ::arguments
  (s/map-of ::argument-name ::argument
            :gen-max 2))

(s/def ::argument
  (s/merge
    (s/keys :req [::argument-name])
    ::typed))

(s/def ::typed
  ;; For validation, we only need to know whether a field is required and
  ;; what type it has, while for canonicalisation we need to know how the
  ;; value is nested.
  (s/keys :req [::type-name
                ::non-null?
                ::type-description]))

(s/def ::type-description
  (s/with-gen
    (s/or :unnested ::unnested
          :nested   ::nested)
    #(gen/frequency
       [[9 (s/gen ::unnested)]
        [1 (gen/bind (gen/return nil) (fn [_] (s/gen ::nested)))]])))

(s/def ::nested
  (s/keys :req [::non-null?
                ::type-description]))

(s/def ::unnested
  (s/keys :req [::non-null?
                ::type-name]))

;; ### Interfaces

(s/def ::interfaces
  (s/map-of ::type-name ::interface
            :gen-max 1))

(s/def ::interface
  (s/keys :req [::type-name
                ::implemented-by
                ::valid-fragment-spreads
                ::fields]))

(s/def ::implemented-by
  (s/coll-of ::type-name
             :gen-max 2))

;; ### Union Types

(s/def ::unions
  (s/map-of ::type-name ::union
            :gen-max 1))

(s/def ::union
  (s/keys :req [::type-name
                ::fields
                ::valid-fragment-spreads
                ::union-types]))

(s/def ::union-types
  (s/coll-of ::type-name
             :min-count 1
             :gen-max 3))

;; ### Scalars

(s/def ::scalars
  (s/coll-of ::type-name
             :gen-max 1))

;; ### Directives

(s/def ::directives
  (s/map-of ::directive-name ::type-name
            :gen-max 1))

;; ### Schema Root

(s/def ::schema-root
  (s/map-of ::operation-type ::type-name
            :gen-max 1))
