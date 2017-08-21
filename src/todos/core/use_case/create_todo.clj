(ns todos.core.use-case.create-todo
  (:require [yoose.async :refer :all]
            [todos.core.entity :as entity]
            [todos.core.entity.todo :as todo]
            [todos.core.use-case :as uc]))


(defusecase create-todo [this {:keys [storage]}]
  (let [entity (<in this)]
    (->> entity
         (todo/save storage)
         (uc/result->action :todo/create)
         (>out this))))
