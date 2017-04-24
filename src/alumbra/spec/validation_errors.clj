(ns alumbra.spec.validation-errors
  (:require [clojure.spec :as s]
            [alumbra.spec common type-description]))

;; ## Error Container

(s/def :alumbra/validation-errors
  (s/coll-of :alumbra/validation-error
             :min-count 1
             :gen-max 2))

(s/def :alumbra/validation-error-class
  #{:operation/name-unique
    :operation/lone-anonymous
    :operation/allowed

    :field/name-in-scope
    :field/selection-mergeable
    :field/leaf-selection

    :argument/name-in-scope
    :argument/name-unique
    :argument/required-given

    :fragment/name-unique
    :fragment/type-exists
    :fragment/type-in-scope
    :fragment/on-composite-type
    :fragment/must-be-used
    :fragment/target-exists
    :fragment/acyclic

    :input/field-name-unique
    :input/field-name-in-scope
    :input/required-fields-given

    :directive/exists
    :directive/location-valid
    :directive/name-unique

    :value/type-correct

    :variable/name-unique
    :variable/default-value-correct
    :variable/input-type
    :variable/name-in-operation-scope
    :variable/name-in-fragment-scope
    :variable/must-be-used})

(defmulti ^:private validation-error-class
  :alumbra/validation-error-class)

(s/def :alumbra/validation-error
  (s/merge
    (s/multi-spec validation-error-class :alumbra/validation-error-class)
    (s/keys :req [:alumbra/locations])))

;; ### Operations

(defmethod validation-error-class :operation/name-unique
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/operation-name]))

(defmethod validation-error-class :operation/allowed
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/operation-type
                :alumbra/operation-name]))

(defmethod validation-error-class :operation/lone-anonymous
  [_]
  (s/keys :req [:alumbra/validation-error-class]))

;; ### Fields

(defmethod validation-error-class :field/name-in-scope
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/field-name
                :alumbra/containing-type-name
                :alumbra/valid-field-names]))

(defmethod validation-error-class :field/selection-mergeable
  [_]
  ;; TODO
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/containing-type-name]))

(defmethod validation-error-class :field/leaf-selection
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/field-name
                :alumbra/containing-type-name]))

(s/def :alumbra/containing-type-name
  :alumbra/type-name)

(s/def :alumbra/field-names
  (s/coll-of :alumbra/field-name
             :into #{}
             :gen-max 3))

(s/def :alumbra/valid-field-names
  :alumbra/field-names)

;; ### Arguments

(defmethod validation-error-class :argument/name-in-scope
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/field-name
                :alumbra/containing-type-name
                :alumbra/argument-name]))

(defmethod validation-error-class :argument/name-unique
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/field-name
                :alumbra/containing-type-name
                :alumbra/argument-name]))

(defmethod validation-error-class :argument/required-given
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/field-name
                :alumbra/containing-type-name
                :alumbra/required-argument-names]))

(s/def :alumbra/argument-type-name
  :alumbra/type-name)

(s/def :alumbra/required-argument-names
  (s/coll-of :alumbra/argument-name
             :gen-max 5
             :into #{}))

;; ### Fragments

(defmethod validation-error-class :fragment/name-unique
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/fragment-name]))

(defmethod validation-error-class :fragment/type-exists
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/fragment-type-name]
          :opt [:alumbra/fragment-name]))

(defmethod validation-error-class :fragment/type-in-scope
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/fragment-type-name
                :alumbra/containing-type-name]
          :opt [:alumbra/fragment-name]))

(defmethod validation-error-class :fragment/on-composite-type
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/fragment-type-name]
          :opt [:alumbra/fragment-name]))

(defmethod validation-error-class :fragment/must-be-used
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/fragment-name]))

(defmethod validation-error-class :fragment/target-exists
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/fragment-name]))

(defmethod validation-error-class :fragment/acyclic
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/cycle-fragment-names
                :alumbra/cycle-fragment-edges]))

(s/def :alumbra/fragment-type-name
  :alumbra/type-name)

(s/def :alumbra/cycle-fragment-names
  (s/coll-of :alumbra/fragment-name
             :min-count 1
             :gen-max 3
             :into #{}))

(s/def :alumbra/cycle-fragment-edges
  (s/map-of :alumbra/fragment-name :alumbra/cycle-fragment-names
            :gen-max 3))

;; ### Input

(defmethod validation-error-class :input/field-name-unique
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/field-name]))

(defmethod validation-error-class :input/field-name-in-scope
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/field-name
                :alumbra/input-type-name
                :alumbra/valid-input-field-names]))

(defmethod validation-error-class :input/required-fields-given
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/input-type-name
                :alumbra/required-input-field-names]))

(s/def :alumbra/input-type-name
  :alumbra/type-name)

(s/def :alumbra/required-input-field-names
  (s/coll-of :alumbra/field-name
             :gen-max 5
             :into #{}))

(s/def :alumbra/valid-input-field-names
  (s/coll-of :alumbra/field-name
             :gen-max 5
             :into #{}))

;; ### Directive

(defmethod validation-error-class :directive/exists
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/directive-name]))

(defmethod validation-error-class :directive/location-valid
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/directive-name]
          :opt [:alumbra/field-name
                :alumbra/fragment-name
                :alumbra/fragment-type-name]))

(defmethod validation-error-class :directive/name-unique
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/directive-name]
          :opt [:alumbra/field-name
                :alumbra/fragment-name
                :alumbra/fragment-type-name]))

;; ### Variables

(defmethod validation-error-class :variable/name-unique
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/variable-name
                :alumbra/operation-name]))

(defmethod validation-error-class :variable/default-value-correct
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/value
                :alumbra/variable-name
                :alumbra/type-description
                :alumbra/operation-name]))

(defmethod validation-error-class :variable/input-type
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/variable-name
                :alumbra/type-description
                :alumbra/operation-name]))

(defmethod validation-error-class :variable/name-in-operation-scope
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/variable-name
                :alumbra/operation-name]))

(defmethod validation-error-class :variable/name-in-fragment-scope
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/variable-name
                :alumbra/fragment-name]))

(defmethod validation-error-class :variable/must-be-used
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/variable-name
                :alumbra/operation-name]))

;; ### Types

(defmethod validation-error-class :value/type-correct
  [_]
  (s/keys :req [:alumbra/validation-error-class
                :alumbra/value
                :alumbra/type-description]))
