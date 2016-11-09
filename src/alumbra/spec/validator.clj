(ns alumbra.spec.validator
  (:require [clojure.spec :as s]
            [alumbra.spec document]))

;; ## Error Container

(s/def :validator/errors
  (s/coll-of :validator/error
             :gen-max 2))

(s/def :validator/error-class
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
  :validator/error-class)

(s/def :validator/error
  (s/merge
    (s/multi-spec error-class :validator/error-class)
    (s/keys :req [:validator/locations])))

;; ## Location

(s/def :validator/locations
  (s/coll-of :validator/location
             :gen-max 1))

(s/def :validator/location
  (s/keys :req [:validator/row
                :validator/column
                :validator/index]))

(s/def :validator/row
  :graphql/row)

(s/def :validator/column
  :graphql/column)

(s/def :validator/index
  :graphql/index)

;; ## Error Metadata

;; ### Operations

(defmethod error-class :operation/name-unique
  [_]
  (s/keys :req [:validator/error-class
                :validator/operation-name]))

(defmethod error-class :operation/lone-anonymous
  [_]
  (s/keys :req [:validator/error-class]))

(s/def :validator/operation-name
  :graphql/operation-name)

;; ### Fields

(defmethod error-class :field/selection-in-scope
  [_]
  (s/keys :req [:validator/error-class
                :validator/field-name
                :validator/containing-type-name
                :validator/valid-field-names]))

(defmethod error-class :field/selection-mergeable
  [_]
  ;; TODO
  (s/keys :req [:validator/error-class
                :validator/containing-type-name]))

(defmethod error-class :field/leaf-selection
  [_]
  (s/keys :req [:validator/error-class
                :validator/field-name
                :validator/containing-type-name]))

(s/def :validator/field-name
  :graphql/field-name)

(s/def :validator/containing-type-name
  :graphql/type-name)

(s/def :validator/field-names
  (s/coll-of :validator/field-name
             :into #{}
             :gen-max 3))

(s/def :validator/valid-field-names
  :validator/field-names)

;; ### Arguments

(defmethod error-class :argument/name-in-scope
  [_]
  (s/keys :req [:validator/error-class
                :validator/field-name
                :validator/containing-type-name
                :validator/argument-name]))

(defmethod error-class :argument/name-unique
  [_]
  (s/keys :req [:validator/error-class
                :validator/field-name
                :validator/containing-type-name
                :validator/argument-name]))

(defmethod error-class :argument/type-correct
  [_]
  (s/keys :req [:validator/error-class
                :validator/field-name
                :validator/containing-type-name
                :validator/argument-name]))

(defmethod error-class :argument/required-given
  [_]
  (s/keys :req [:validator/error-class
                :validator/field-name
                :validator/containing-type-name
                :validator/required-argument-names]))

(s/def :validator/argument-name
  :graphql/argument-name)

(s/def :validator/argument-type-name
  :graphql/type-name)

(s/def :validator/required-argument-names
  (s/coll-of :validator/argument-name
             :gen-max 5
             :into #{}))

;; ### Fragments

(defmethod error-class :fragment/name-unique
  [_]
  (s/keys :req [:validator/error-class
                :validator/fragment-name]))

(defmethod error-class :fragment/type-exists
  [_]
  (s/keys :req [:validator/error-class
                :validator/fragment-type-name]
          :opt [:validator/fragment-name]))

(defmethod error-class :fragment/type-in-scope
  [_]
  (s/keys :req [:validator/error-class
                :validator/fragment-type-name
                :validator/containing-type-name]
          :opt [:validator/fragment-name]))

(defmethod error-class :fragment/on-composite-type
  [_]
  (s/keys :req [:validator/error-class
                :validator/fragment-type-name]
          :opt [:validator/fragment-name]))

(defmethod error-class :fragment/must-be-used
  [_]
  (s/keys :req [:validator/error-class
                :validator/fragment-name]))

(defmethod error-class :fragment/target-exists
  [_]
  (s/keys :req [:validator/error-class
                :validator/fragment-name]))

(defmethod error-class :fragment/acyclic
  [_]
  (s/keys :req [:validator/error-class
                :validator/cycle-fragment-names
                :validator/cycle-fragment-edges]))

(s/def :validator/fragment-type-name
  :graphql/type-name)

(s/def :validator/fragment-name
  :graphql/fragment-name)

(s/def :validator/cycle-fragment-names
  (s/coll-of :validator/fragment-name
             :min-count 1
             :gen-max 3
             :into #{}))

(s/def :validator/cycle-fragment-edges
  (s/map-of :validator/fragment-name :validator/cycle-fragment-names
            :gen-max 3))

;; ### Input

(defmethod error-class :input/field-name-unique
  [_]
  (s/keys :req [:validator/error-class
                :validator/field-name]))

;; ### Directive

(defmethod error-class :directive/exists
  [_]
  (s/keys :req [:validator/error-class
                :validator/directive-name]))

(defmethod error-class :directive/location-valid
  [_]
  (s/keys :req [:validator/error-class
                :validator/directive-name]
          :opt [:validator/field-name
                :validator/fragment-name
                :validator/fragment-type-name]))

(defmethod error-class :directive/name-unique
  [_]
  (s/keys :req [:validator/error-class
                :validator/directive-name]
          :opt [:validator/field-name
                :validator/fragment-name
                :validator/fragment-type-name]))

(s/def :validator/directive-name
  :graphql/directive-name)

;; ### Variables

(defmethod error-class :variable/name-unique
  [_]
  (s/keys :req [:validator/error-class
                :validator/operation-name
                :validator/variable-name]))

(defmethod error-class :variable/default-value-correct
  [_]
  (s/keys :req [:validator/error-class
                :validator/operation-name
                :validator/variable-name
                :validator/variable-type-name]))

(defmethod error-class :variable/input-type
  [_]
  (s/keys :req [:validator/error-class
                :validator/operation-name
                :validator/variable-name
                :validator/variable-type-name]))

(defmethod error-class :variable/exists
  [_]
  (s/keys :req [:validator/error-class
                :validator/variable-name]))

(defmethod error-class :variable/must-be-used
  [_]
  (s/keys :req [:validator/error-class
                :validator/operation-name
                :validator/variable-name]))

(defmethod error-class :variable/type-correct
  [_]
  (s/keys :req [:validator/error-class
                :validator/operation-name
                :validator/variable-name
                :validator/variable-type-name
                :validator/argument-type-name]))

(s/def :validator/variable-name
  :graphql/variable-name)

(s/def :validator/variable-type-name
  :graphql/type-name)
