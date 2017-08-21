(ns todos.delivery.api.delete
  (:require [ring.util.response :refer [response]]
            [yoose.core :as yoose]
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
       (yoose/push! delete-todo)
       yoose/pull!!
       action->response))
