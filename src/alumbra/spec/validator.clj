(ns alumbra.spec.validator
  (:require [clojure.spec :as s]
            [alumbra.spec.common :as common]))

(common/import-specs
  ::argument-name
  ::directive-name
  ::field-name
  ::fragment-name
  ::operation-name
  ::variable-name
  ::type-name
  ::row
  ::column
  ::index)

;; ## Error Container

(s/def ::errors
  (s/coll-of ::error
             :min-count 1
             :gen-max 2))

(s/def ::error-class
  #{:operation/name-unique
    :operation/lone-anonymous
    :operation/required-variables-given

    :field/selection-in-scope
    :field/selection-mergeable
    :field/leaf-selection

    :argument/name-in-scope
    :argument/name-unique
    :argument/type-correct
    :argument/required-given

    :fragment/name-unique
    :fragment/type-exists
    :fragment/type-in-scope
    :fragment/on-composite-type
    :fragment/must-be-used
    :fragment/target-exists
    :fragment/acyclic

    :input/field-name-unique

    :directive/exists
    :directive/location-valid
    :directive/name-unique

    :variable/name-unique
    :variable/default-value-correct
    :variable/input-type
    :variable/exists
    :variable/must-be-used
    :variable/type-correct})

(defmulti ^:private error-class
  ::error-class)

(s/def ::error
  (s/merge
    (s/multi-spec error-class ::error-class)
    (s/keys :req [::locations])))

;; ## Location

(s/def ::locations
  (s/coll-of ::location
             :gen-max 1))

(s/def ::location
  ::common/metadata)

;; ## Error Metadata

;; ### Operations

(defmethod error-class :operation/name-unique
  [_]
  (s/keys :req [::error-class
                ::operation-name]))

(defmethod error-class :operation/lone-anonymous
  [_]
  (s/keys :req [::error-class]))

;; ### Fields

(defmethod error-class :field/selection-in-scope
  [_]
  (s/keys :req [::error-class
                ::field-name
                ::containing-type-name
                ::valid-field-names]))

(defmethod error-class :field/selection-mergeable
  [_]
  ;; TODO
  (s/keys :req [::error-class
                ::containing-type-name]))

(defmethod error-class :field/leaf-selection
  [_]
  (s/keys :req [::error-class
                ::field-name
                ::containing-type-name]))

(s/def ::containing-type-name
  ::type-name)

(s/def ::field-names
  (s/coll-of ::field-name
             :into #{}
             :gen-max 3))

(s/def ::valid-field-names
  ::field-names)

;; ### Arguments

(defmethod error-class :argument/name-in-scope
  [_]
  (s/keys :req [::error-class
                ::field-name
                ::containing-type-name
                ::argument-name]))

(defmethod error-class :argument/name-unique
  [_]
  (s/keys :req [::error-class
                ::field-name
                ::containing-type-name
                ::argument-name]))

(defmethod error-class :argument/type-correct
  [_]
  (s/keys :req [::error-class
                ::field-name
                ::containing-type-name
                ::argument-name]))

(defmethod error-class :argument/required-given
  [_]
  (s/keys :req [::error-class
                ::field-name
                ::containing-type-name
                ::required-argument-names]))

(s/def ::argument-type-name
  ::type-name)

(s/def ::required-argument-names
  (s/coll-of ::argument-name
             :gen-max 5
             :into #{}))

;; ### Fragments

(defmethod error-class :fragment/name-unique
  [_]
  (s/keys :req [::error-class
                ::fragment-name]))

(defmethod error-class :fragment/type-exists
  [_]
  (s/keys :req [::error-class
                ::fragment-type-name]
          :opt [::fragment-name]))

(defmethod error-class :fragment/type-in-scope
  [_]
  (s/keys :req [::error-class
                ::fragment-type-name
                ::containing-type-name]
          :opt [::fragment-name]))

(defmethod error-class :fragment/on-composite-type
  [_]
  (s/keys :req [::error-class
                ::fragment-type-name]
          :opt [::fragment-name]))

(defmethod error-class :fragment/must-be-used
  [_]
  (s/keys :req [::error-class
                ::fragment-name]))

(defmethod error-class :fragment/target-exists
  [_]
  (s/keys :req [::error-class
                ::fragment-name]))

(defmethod error-class :fragment/acyclic
  [_]
  (s/keys :req [::error-class
                ::cycle-fragment-names
                ::cycle-fragment-edges]))

(s/def ::fragment-type-name
  ::type-name)

(s/def ::cycle-fragment-names
  (s/coll-of ::fragment-name
             :min-count 1
             :gen-max 3
             :into #{}))

(s/def ::cycle-fragment-edges
  (s/map-of ::fragment-name ::cycle-fragment-names
            :gen-max 3))

;; ### Input

(defmethod error-class :input/field-name-unique
  [_]
  (s/keys :req [::error-class
                ::field-name]))

;; ### Directive

(defmethod error-class :directive/exists
  [_]
  (s/keys :req [::error-class
                ::directive-name]))

(defmethod error-class :directive/location-valid
  [_]
  (s/keys :req [::error-class
                ::directive-name]
          :opt [::field-name
                ::fragment-name
                ::fragment-type-name]))

(defmethod error-class :directive/name-unique
  [_]
  (s/keys :req [::error-class
                ::directive-name]
          :opt [::field-name
                ::fragment-name
                ::fragment-type-name]))

;; ### Variables

(defmethod error-class :variable/name-unique
  [_]
  (s/keys :req [::error-class
                ::operation-name
                ::variable-name]))

(defmethod error-class :variable/default-value-correct
  [_]
  (s/keys :req [::error-class
                ::operation-name
                ::variable-name
                ::variable-type-name]))

(defmethod error-class :variable/input-type
  [_]
  (s/keys :req [::error-class
                ::operation-name
                ::variable-name
                ::variable-type-name]))

(defmethod error-class :variable/exists
  [_]
  (s/keys :req [::error-class
                ::variable-name]))

(defmethod error-class :variable/must-be-used
  [_]
  (s/keys :req [::error-class
                ::operation-name
                ::variable-name]))

(defmethod error-class :variable/type-correct
  [_]
  (s/keys :req [::error-class
                ::operation-name
                ::variable-name
                ::variable-type-name
                ::argument-type-name]))

(s/def ::variable-type-name
  ::type-name)
