(ns todos.delivery.api.create
  (:require [clojure.spec.alpha :as s]
            [ring.util.response :refer [response]]
            [todos.delivery.api.spec :as ts]
            [todos.delivery.api.json :refer [todo->json]]
            [todos.delivery.use-cases :refer [create-todo]]
            [todos.core.entity.todo :as t]
            [todos.core.use-case :as uc]
            [todos.core.action :as action]))


;; @todo maybe just parse the explanation from spec
(def bad-request
  {:status 422
   :body   {:error "Invalid title or complete? keys"}})


(defn- action->response
  "Converts an action to a response"
  [{:keys [::action/error? ::action/payload]}]
  (if error?
    {:status 500
     :body   {:error "There was an error creating the todo"}}
    {:status 201
     :body   {:data (todo->json (:result payload))}}))


(defn- create
  "Creates a new todo from the request"
  [{:keys [title complete?]}]
  (let [todo (t/make-todo title)]
    (->> (if complete? (t/mark-complete todo) todo)
         (uc/put! create-todo)
         uc/take!!
         action->response)))


(defn respond
  "Handles a request to create a new todo"
  [{:keys [body]}]
  (let [payload (s/conform ::ts/todo-request body)]
    (if (= payload ::s/invalid)
      bad-request
      (create payload))))
