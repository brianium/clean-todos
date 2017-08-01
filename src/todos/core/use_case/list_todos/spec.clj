(ns todos.core.use-case.list-todos.spec
  (:require [clojure.spec.alpha :as s]
            [todos.core.use-case.list-todos :as list-todos]
            [todos.core.use-case.spec :as use-case]))


(s/fdef list-todos/list-todos
  :args (s/cat :dependencies ::use-case/dependencies)
  :ret  ::use-case/use-case)
