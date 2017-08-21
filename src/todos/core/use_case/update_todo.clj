(ns todos.core.use-case.update-todo
  (:require [yoose.async :refer :all]
            [todos.core.entity :as entity]
            [todos.core.entity.todo :as todo]
            [todos.core.use-case :as uc]))


(defn- strip-nils
  "Removes nils from a todo"
  [todo]
  (into {} (filter (comp some? val) todo)))


(defusecase update-todo [this {:keys [storage]}]
  (let [[id todo] (<in this)
        current   (todo/fetch storage id)]
    (if (entity/storage-error? current)
      (>out this (uc/result->action :todo/update current))
      (->> {::entity/id id}
           (merge todo)
           strip-nils
           (merge current)
           (todo/save storage)
           (uc/result->action :todo/update)
           (>out this)))))
