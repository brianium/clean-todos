(ns todos.delivery.api.json
  (:require [todos.core.entity :as e]
            [todos.core.entity.todo :as t]))


(defn todo->json
  "Converts a todo entity to json for humans (no namespaced keys)"
  [todo]
  {:id          (::e/id todo)
   :title       (::t/title todo)
   :complete?   (::t/complete? todo)
   :created_at  (::t/created-at todo)
   :modified_at (::t/modified-at todo)})
