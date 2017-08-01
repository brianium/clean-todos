(ns todos.core.use-case.create-todo.spec
  (:require [clojure.spec.alpha :as s]
            [todos.core.use-case.create-todo :as create-todo]
            [todos.core.use-case.spec :as use-case]))


(s/fdef create-todo/create-todo
  :args (s/cat :dependencies ::use-case/dependencies)
  :ret  ::use-case/use-case)
