(ns alumbra.spec
  (:require [alumbra.spec
             analyzed-schema
             canonical-operation
             document
             parser-errors
             schema
             validation-errors]
            [clojure.spec :as s]))

;; ## Values

(let [stream? #(instance? java.io.InputStream %)]
  (def ^:private parser-input?
    (s/alt :string string?
           :stream stream?)))

(s/def ::parser-errors
  (s/keys :req [:alumbra/parser-errors]))

(s/def ::validation-errors
  (s/keys :req [:alumbra/validation-errors]))

;; ## Components

(s/def ::document-parser
  (s/fspec
    :args (s/cat :document parser-input?)
    :ret  (s/alt :success :alumbra/document
                 :failure ::parser-errors)))

(s/def ::schema-parser
  (s/fspec
    :args (s/cat :document parser-input?)
    :ret  (s/alt :success :alumbra/schema
                 :failure ::parser-errors)))

(s/def ::analyzer
  (s/fspec
    :args (s/cat :schema :alumbra/schema)
    :ret  :alumbra/analyzed-schema))

(s/def ::validator
  (s/fspec
    :args (s/cat :document  :alumbra/document
                 :variables (s/? map?))
    :ret  (s/alt :success nil?
                 :failure ::validation-errors)))

(s/def ::canonicalizer
  (s/fspec
    :args (s/cat :document       :alumbra/document
                 :operation-name string?
                 :variables      map?)
    :ret  :alumbra/canonical-operation))

(s/def ::executor
  (s/fspec
    :args (s/cat :operation :alumbra/canonical-operation)
    :ret  map?))
