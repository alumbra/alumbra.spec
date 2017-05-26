(ns alumbra.spec.parser-errors
  (:require [clojure.spec.alpha :as s]
            [alumbra.spec common]))

(s/def :alumbra/parser-errors
  (s/coll-of :alumbra/parser-error
             :min-count 1
             :gen-max 1))

(s/def :alumbra/parser-error
  (s/keys :req [:alumbra/location
                :alumbra/error-message]))
