(ns alumbra.spec.canonical-operation
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [alumbra.spec.common]))

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
                   ::directives
                   :alumbra/operation-name
                   :alumbra/operation-type]))

;; ## Selection

(s/def ::selection-set
  (s/coll-of ::selection
             :min-count 1
             :gen-max 2))

(s/def ::selection
  (s/with-gen
    (s/or :field ::field
          :block ::block)
    #(gen/frequency
       [[9 (s/gen ::field)]
        [1 (gen/bind (gen/return nil) (fn [_] (s/gen ::block)))]])))

;; ### Field Selection

(s/def ::field-type
  #{:leaf :object :list})

(defmulti field-spec :field-type)

(defmethod field-spec :leaf
  [_]
  (s/keys :req-un [:alumbra/type-name
                   :alumbra/non-null?
                   ::field-type]))

(defmethod field-spec :object
  [_]
  (s/keys :req-un [:alumbra/non-null?
                   :alumbra/type-name
                   ::field-type
                   ::selection-set]))

(defmethod field-spec :list
  [_]
  (s/keys :req-un [:alumbra/non-null?
                   ::field-type
                   ::field-spec]))

(s/def ::field-spec
  (s/multi-spec field-spec :field-type))

(s/def ::field
  (s/merge
    ::field-spec
    (s/keys :req-un [:alumbra/field-name
                     ::arguments
                     ::directives
                     :alumbra/field-alias])))

;; ### Block

(s/def ::block
  (s/keys :req-un [::directives
                   ::selection-set]
          :opt-un [::type-condition]))

(s/def ::type-condition
  (s/coll-of :alumbra/type-name
             :into #{}
             :gen-max 1
             :min-count 1))

;; ## Arguments

(s/def ::arguments
  (s/map-of :alumbra/argument-name ::value-wrapper
            :gen-max 2))

;; ## Values

(s/def ::value-wrapper
  (s/or :scalar ::scalar
        :object ::object
        :list   ::list))

(s/def ::scalar
  (s/keys :req-un [:alumbra/type-name
                   :alumbra/non-null?
                   ::value]))

(s/def ::value
  (s/or :string   string?
        :integer  integer?
        :float    float?
        :boolean  boolean?
        :null     nil?))

(s/def ::object
  (s/map-of string? ::value-wrapper
            :gen-max 2))

(s/def ::list
  (s/coll-of ::value-wrapper
             :gen-max 2))

;; ## Directives

(s/def ::directives
  (s/coll-of ::directive
             :gen-max 1))

(s/def ::directive
  (s/keys :req-un [:alumbra/directive-name
                   ::arguments]))
