(ns todos.core.use-case.list-todos
  (:require [yoose.async :refer :all]
            [todos.core.entity.todo :as todo]
            [todos.core.use-case :as uc]))


(defusecase list-todos [this {:keys [storage]}]
  (let [status (<in this)]
    (->> (todo/all storage)
         (todo/filter-todos status)
         (uc/result->action :todo/list)
         (>out this))))
