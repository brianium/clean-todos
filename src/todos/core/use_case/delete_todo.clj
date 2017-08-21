(ns todos.core.use-case.delete-todo
  (:require [yoose.async :refer :all]
            [todos.core.use-case :as uc]
            [todos.core.entity.todo :as todo]))


(defusecase delete-todo [this {:keys [storage]}]
  (let [id (<in this)]
    (->> id
         (todo/delete storage)
         (uc/result->action :todo/remove)
         (>out this))))
