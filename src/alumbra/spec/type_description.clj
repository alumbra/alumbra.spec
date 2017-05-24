(ns alumbra.spec.type-description
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [alumbra.spec common]))

(s/def :alumbra/type-description
  (s/with-gen
    (s/or :unnested ::unnested
          :nested   ::nested)
    #(gen/frequency
       [[9 (s/gen ::unnested)]
        [1 (gen/bind (gen/return nil) (fn [_] (s/gen ::nested)))]])))

(s/def ::nested
  (s/keys :req-un [:alumbra/non-null?
                   :alumbra/type-description]))

(s/def ::unnested
  (s/keys :req-un [:alumbra/non-null?
                   :alumbra/type-name]))
