(ns alumbra.spec.canonical-document
  (:require [clojure.spec :as s]
            [alumbra.spec.common :as common]))

(common/import-specs
  ::argument-name
  ::type-name
  ::field-name
  ::non-null?
  ::operation-name
  ::operation-type
  ::field-alias)

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

(s/def ::document
  (s/and
    (s/coll-of ::operation
               :min-count 1
               :gen-max 3)
    #(or (= (count %) 1)
         (every? ::operation-name %))))

;; ## Operation

(s/def ::operation
  (s/keys :req [::selection-set
                ::operation-type]
          :opt [::operation-name]))

;; ## Selection

(s/def ::selection-set
  (s/map-of ::field-alias ::field
            :min-count 1
            :gen-max 2))

(s/def ::value-type
  #{:integer :float :string
    :boolean :enum :object :list})

(s/def ::field-type
  #{:leaf :object :list})

(defmulti canonical-field ::field-type)

(defmethod canonical-field :leaf
  [_]
  (s/keys :req [::field-type
                ::value-type
                ::non-null?
                ::arguments]))

(defmethod canonical-field :object
  [_]
  (s/keys :req [::field-type
                ::non-null?
                ::selection-set]))

(defmethod canonical-field :list
  [_]
  (s/keys :req [::field-type
                ::non-null?
                ::field]))

(s/def ::field
  (s/merge
    (s/multi-spec canonical-field ::field-type)
    (s/keys :req [::field-name]
            :opt [::field-type-condition])))

(s/def ::field-type-condition
  ::type-name)

;; ## Arguments

(s/def ::arguments
  (s/map-of ::argument-name ::value
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
