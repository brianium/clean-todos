(ns todos.delivery.api.list
  (:require [ring.util.response :refer [response]]
            [todos.delivery.use-cases :refer [list-todos]]
            [todos.core.use-case :as uc]
            [todos.core.entity.todo :as t]
            [todos.core.entity :as e]
            [todos.core.action :as action]))


(defn- status
  "Gets a todo status from the query string"
  [{:keys [query-params]}]
  (-> (get query-params "status")
      #{"completed" "active"}
      (or "all")
      keyword))


(defn- todo->json
  "Converts a todo entity to json for humans (no namespaced keys)"
  [todo]
  {:id          (::e/id todo)
   :title       (::t/title todo)
   :complete    (::t/complete? todo)
   :created_at  (::t/created-at todo)
   :modified_at (::t/modified-at todo)})


(defn- action->json
  "Converts an action to a json friendly format"
  [{{:keys [result]} ::action/payload}]
  (map todo->json result))


(defn respond
  "Handles listing todos as json"
  [request]
  (->> request
    status
    (uc/put! list-todos)
    uc/take!!
    action->json
    (assoc {} :data)
    response))
