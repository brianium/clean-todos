(ns todos.core.use-case.delete-todo.spec
  (:require [clojure.spec.alpha :as s]
            [todos.core.use-case.delete-todo :refer [delete-todo]]
            [todos.core.use-case.spec :as use-case]))


(s/fdef delete-todo
  :args (s/cat :dependencies ::use-case/dependencies)
  :ret  ::use-case/use-case)
