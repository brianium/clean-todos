(ns todos.core.use-case
  (:require [todos.core.entity :as entity]
            [todos.core.action :as action]))


(defn result->action
  "Creates an action for the result of creating a new todo"
  [type result]
  (let [payload { :result result }]
    (if (entity/storage-error? result)
      (action/make-error type payload)
      (action/make-action type payload))))
