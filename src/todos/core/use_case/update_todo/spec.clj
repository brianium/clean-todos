(ns todos.core.use-case.update-todo.spec
  (:require [clojure.spec.alpha :as s]
            [todos.core.use-case.update-todo :as update-todo]
            [todos.core.use-case.spec :as use-case]))


(s/fdef update-todo/update-todo
  :args (s/cat :dependencies ::use-case/dependencies)
  :ret  ::use-case/use-case)
