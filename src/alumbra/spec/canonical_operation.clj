(ns alumbra.spec.canonical-operation
  (:require [clojure.spec :as s]
            [alumbra.spec common]))

;; ## Canonical Operation
;;
;; The idea here is that, for execution, the concepts of fragments or variables
;; are no longer relevant. We just need a resolved description of the query,
;; providing:
;;
;; - which fields are requested,
;; - which leaves are lists, objects, scalars.
;; - which scalar type leaves have.
;; - which fields can be null, which can't.
;; - which fields have type conditions (from fragment and inline spreads).
;; - which (groups of) fields have directives assigned to them.

;; ## Operation

(s/def :alumbra/canonical-operation
  (s/keys :req-un [::selection-set
                   :alumbra/operation-type]
          :opt-un [:alumbra/operation-name
                   ::directives]))

;; ## Selection

(s/def ::selection-set
  (s/coll-of ::field
             :min-count 1
             :gen-max 2))

(s/def ::field-type
  #{:leaf :object :list :conditional-block})

(defmulti field :field-type)

(defmethod field :leaf
  [_]
  (s/keys :req-un [:alumbra/field-name
                   :alumbra/value-type
                   :alumbra/non-null?
                   ::field-type]
          :opt-un [::arguments
                   ::directives]))

(defmethod field :object
  [_]
  (s/keys :req-un [:alumbra/field-name
                   :alumbra/non-null?
                   ::field-type
                   ::selection-set]
          :opt-un [::directives]))

(defmethod field :list
  [_]
  (s/keys :req-un [:alumbra/field-name
                   :alumbra/non-null?
                   ::field-type
                   ::field]
          :opt-un [::directives]))

(defmethod field :conditional-block
  [_]
  (s/keys :req-un [::field-type
                   ::type-condition
                   ::field]
          :opt-un [::directives]))

(s/def ::field
  (s/multi-spec field :field-type))

(s/def ::type-condition
  :alumbra/type-name)

;; ## Arguments

(s/def ::arguments
  (s/map-of :alumbra/argument-name ::value
            :gen-max 2))

;; ## Values

(s/def ::value
  (s/or :string   string?
        :enum     keyword?
        :variable symbol?
        :integer  integer?
        :float    float?
        :boolean  boolean?
        :object   ::object
        :list     ::list))

(s/def ::object
  (s/map-of string? ::value
            :gen-max 2))

(s/def ::list
  (s/coll-of ::value
             :gen-max 2))

;; ## Directives

(s/def ::directives
  (s/coll-of ::directive
             :min-count 1
             :gen-max 1))

(s/def ::directive
  (s/keys :req-un [:alumbra/directive-name]
          :opt-un [::arguments]))
