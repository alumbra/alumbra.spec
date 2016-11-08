(ns alumbra.spec.canonical-document
  (:require [clojure.spec :as s]
            [alumbra.spec document]))

(s/def :graphql/canonical-document
  (s/and
    (s/coll-of :graphql/canonical-operation
               :min-count 1
               :gen-max 3)
    #(or (= (count %) 1)
         (every? :graphql/operation-name %))))

(s/def :graphql/canonical-operation
  (s/keys :req [:graphql/canonical-selection]
          :opt [:graphql/operation-name]))

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
    (s/keys :req [:graphql/field-name])))

(s/def :graphql/canonical-arguments
  (s/map-of :graphql/argument-name :graphql/canonical-value
            :gen-max 2))

(s/def :graphql/canonical-value
  (s/or :string   string?
        :enum     keyword?
        :variable symbol?
        :integer  integer?
        :float    float?
        :boolean  boolean?))
