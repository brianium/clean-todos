(ns todos.core.use-case.create-todo.spec
  (:require [clojure.spec.alpha :as s]
            [todos.core.use-case.create-todo :as create-todo]
            [todos.core.use-case.spec :as use-case]
            [todos.core.entity.todo.spec :as todo]
            [todos.core.action.spec :as action]))


(s/def ::in           ::use-case/read-port)
(s/def ::out          ::use-case/write-port)
(s/def ::storage      ::todo/storage)
(s/def ::dependencies (s/keys :req-un [::in ::out ::storage]))


(s/fdef create-todo/result->action
  :args  (s/cat :result ::todo/storage-result)
  :ret   ::action/action)


(s/fdef create-todo/create-todo
  :args (s/cat :dependencies ::dependencies)
  :ret  ::use-case/use-case)
