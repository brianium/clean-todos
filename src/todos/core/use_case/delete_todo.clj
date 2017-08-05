(ns todos.core.use-case.delete-todo
  (:require [clojure.core.async :refer [go-loop >! <!]]
            [todos.core.use-case :as uc]
            [todos.core.entity.todo :as todo]))


(defn delete-todo
  "Creates a use case for deleting a todo"
  [{:keys [in out storage] :as dependencies}]
  (let [use-case (uc/make-use-case in out)]
    (go-loop []
      (let [id (<! in)]
        (->> id
          (todo/delete storage)
          (uc/result->action :todo/remove)
          (>! out)))
      (recur))
    use-case))
