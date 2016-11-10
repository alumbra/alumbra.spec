(ns alumbra.spec.canonical-document
  (:require [clojure.spec :as s]
            [alumbra.spec common]))

;; ## Document
;;
;; The idea here is that, for execution, the concepts of fragments or variables
;; are no longer relevant. We just need a resolved description of the query,
;; providing:
;;
;; - which fields are requested,
;; - which leaves are lists, objects, scalars.
;; - which scalar type leaves have.
;; - which fields can be null, which can't.

(s/def :alumbra/canonical-document
  (s/and
    (s/coll-of ::operation
               :min-count 1
               :gen-max 3)
    #(or (= (count %) 1)
         (every? :alumbra/operation-name %))))

;; ## Operation

(s/def ::operation
  (s/keys :req-un [::selection-set
                   :alumbra/operation-type]
          :opt-un [:alumbra/operation-name]))

;; ## Selection

(s/def ::selection-set
  (s/map-of :alumbra/field-alias ::field
            :min-count 1
            :gen-max 2))

(s/def ::field-type
  #{:leaf :object :list})

(defmulti field ::field-type)

(defmethod field :leaf
  [_]
  (s/keys :req-un [:alumbra/value-type
                   :alumbra/non-null?
                   ::field-type
                   ::arguments]))

(defmethod field :object
  [_]
  (s/keys :req-un [:alumbra/non-null?
                   ::field-type
                   ::selection-set]))

(defmethod field :list
  [_]
  (s/keys :req-un [:alumbra/non-null?
                   ::field-type
                   ::field]))

(s/def ::field
  (s/merge
    (s/multi-spec field ::field-type)
    (s/keys :req-un [:alumbra/field-name]
            :opt-un [::type-condition])))

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
