(ns todos.core.use-case.spec
  (:require [clojure.spec.alpha :as s]
            [todos.core.use-case :as use-case]
            [todos.core.action.spec :as action]
            [todos.core.entity.spec :as entity]))


(s/fdef use-case/result->action
  :args  (s/cat :type keyword? :result ::entity/storage-result)
  :ret   ::action/action)
