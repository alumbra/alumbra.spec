(ns alumbra.spec.common
  (:require [clojure.spec :as s]))

;; ## Name

(s/def ::name
  (s/and string? #(re-matches #"[_a-zA-Z][_0-9a-zA-Z]*" %)))

(s/def ::operation-name
  ::name)

(s/def ::fragment-name
  (s/and ::name #(not= % "on")))

(s/def ::field-name
  ::name)

(s/def ::field-alias
  ::name)

(s/def ::variable-name
  ::name)

(s/def ::type-name
  ::name)

(s/def ::argument-name
  ::name)

(s/def ::directive-name
  ::name)

;; ## Operation Type

(s/def ::operation-type
  #{"mutation" "query" "subscription"})

;; ## Metadata

(s/def ::metadata
  (s/keys :req-un [::row
                   ::column
                   ::index]))

(s/def ::row
  (s/and integer? #(>= % 0)))

(s/def ::column
  (s/and integer? #(>= % 0)))

(s/def ::index
  (s/and integer? #(>= % 0)))

;; ## Flags

(s/def ::non-null?
  boolean?)

;; ## Alias Macro

(defmacro import-specs
  [& ks]
  `(do
     ~@(for [k ks]
         `(s/def ~k
            ~(keyword "alumbra.spec.common" (name k))))))
