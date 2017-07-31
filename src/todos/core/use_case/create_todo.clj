(ns todos.core.use-case.create-todo
  (:require [clojure.core.async :refer [go-loop <! >!]]
            [todos.core.entity :as entity]
            [todos.core.entity.todo :as todo]
            [todos.core.use-case :as uc]
            [todos.core.action :as action]))


(defn result->action
  "Creates an action for the result of creating a new todo"
  [result]
  (if (entity/storage-error? result)
    (action/make-error result)
    (action/make-action :todo/created {:result result})))


(defn create-todo
  [{:keys [in out storage] :as dependencies}]
  (let [use-case (uc/make-use-case in out)]
    (go-loop []
      (let [entity (<! in)]
        (->> entity
             (todo/insert storage)
             (result->action)
             (>! out)))
      (recur))
    use-case))
