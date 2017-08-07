(ns todos.delivery.api.delete
  (:require [ring.util.response :refer [response]]
            [todos.core.use-case :as uc]
            [todos.delivery.use-cases :refer [delete-todo]]
            [todos.core.action :as action]))


(defn action->response
  "Converts an action to a delete reponse"
  [{{:keys [result]} ::action/payload}]
  (if result
    {:status 204}
    {:status 404
     :body   {:error "todo not found"}}))


(defn respond
  "Deletes a todo and responds"
  [id]
  (->> id
    (uc/put! delete-todo)
    uc/take!!
    action->response))
