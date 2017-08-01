(ns todos.core.entity.spec
  (:require [clojure.spec.alpha :as s]
            [todos.core.entity :as entity]))


(s/def ::entity/id uuid?)
(s/def ::entity (s/keys :req [::entity/id]))
(s/def ::storage-error  keyword?)
(s/def ::storage-result (s/or :data (s/or :entity ::entity :entities (s/* ::entity))
                              :error ::storage-error))


(s/fdef entity/storage-error?
  :args (s/cat :result ::storage-result)
  :ret  boolean?)
