(ns todos.delivery.api.list
  (:require [ring.util.response :refer [response]]
            [todos.delivery.use-cases :refer [list-todos]]
            [todos.core.use-case :as uc]
            [todos.core.entity.todo :as t]
            [todos.core.entity :as e]
            [todos.core.action :as action]
            [todos.delivery.api.json :refer [todo->json]]))


(defn- status
  "Gets a todo status from the query string"
  [{:keys [query-params]}]
  (-> (get query-params "status")
      #{"completed" "active"}
      (or "all")
      keyword))


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
