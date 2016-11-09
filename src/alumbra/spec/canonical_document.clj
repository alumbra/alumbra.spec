(ns alumbra.spec.canonical-document
  (:require [clojure.spec :as s]
            [alumbra.spec document]))

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

(s/def :graphql/canonical-document
  (s/and
    (s/coll-of :graphql/canonical-operation
               :min-count 1
               :gen-max 3)
    #(or (= (count %) 1)
         (every? :graphql/operation-name %))))

;; ## Operation

(s/def :graphql/canonical-operation
  (s/keys :req [:graphql/canonical-selection
                :graphql/operation-type]
          :opt [:graphql/operation-name]))

;; ## Selection

(s/def :graphql/canonical-selection
  (s/map-of :graphql/field-alias :graphql/canonical-field
            :min-count 1
            :gen-max 2))

(s/def :graphql/canonical-field-type
  #{:leaf :object :list})

(defmulti canonical-field :graphql/canonical-field-type)

(defmethod canonical-field :leaf
  [_]
  (s/keys :req [:graphql/canonical-field-type
                :graphql/value-type
                :graphql/non-null?
                :graphql/canonical-arguments]))

(defmethod canonical-field :object
  [_]
  (s/keys :req [:graphql/canonical-field-type
                :graphql/non-null?
                :graphql/canonical-selection]))

(defmethod canonical-field :list
  [_]
  (s/keys :req [:graphql/canonical-field-type
                :graphql/non-null?
                :graphql/canonical-field]))

(s/def :graphql/canonical-field
  (s/merge
    (s/multi-spec canonical-field :graphql/canonical-field-type)
    (s/keys :req [:graphql/field-name]
            :opt [:graphql/canonical-field-type-condition])))

(s/def :graphql/canonical-field-type-condition
  :graphql/type-name)

;; ## Arguments

(s/def :graphql/canonical-arguments
  (s/map-of :graphql/argument-name :graphql/canonical-value
            :gen-max 2))

;; ## Values

(s/def :graphql/canonical-value
  (s/or :string   string?
        :enum     keyword?
        :variable symbol?
        :integer  integer?
        :float    float?
        :boolean  boolean?
        :object   :graphql/canonical-object
        :list     :graphql/canonical-list))

(s/def :graphql/canonical-object
  (s/map-of string? :graphql/canonical-value
            :gen-max 2))

(s/def :graphql/canonical-list
  (s/coll-of :graphql/canonical-value
             :gen-max 2))
