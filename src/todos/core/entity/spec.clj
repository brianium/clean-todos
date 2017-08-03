(ns todos.core.entity.spec
  (:require [clojure.spec.alpha :as s]
            [todos.core.entity :as entity]))


(s/def ::entity/id uuid?)
(s/def ::entity (s/keys :req [::entity/id]))
(s/def ::storage-error  keyword?)
(s/def ::storage-result (s/or :data (s/or :entity ::entity :entities (s/* ::entity))
                              :error ::storage-error))
(s/def ::uuid-string entity/uuid-string?)


(s/fdef entity/storage-error?
  :args (s/cat :result ::storage-result)
  :ret  boolean?)


(s/fdef entity/uuid-string?
  :args (s/cat :str string?)
  :ret  boolean?)


(s/fdef entity/make-uuid
  :args  empty?
  :ret  ::entity/id)


(s/fdef entity/string->uuid
  :args  (s/cat :str ::uuid-string)
  :ret   ::entity/id)
