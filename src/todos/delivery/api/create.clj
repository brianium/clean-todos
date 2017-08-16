(ns todos.delivery.api.create
  (:require [clojure.spec.alpha :as s]
            [ring.util.response :refer [response]]
            [todos.delivery.api.spec :as ts]
            [todos.delivery.api.json :refer [todo->json]]
            [todos.delivery.use-cases :refer [create-todo]]
            [todos.core.entity :as e]
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


(defn- assoc-id
  [id todo]
  (if id
    (assoc todo ::e/id (e/string->uuid id))
    todo))


(defn- create
  "Creates a new todo from the request"
  [{:keys [title complete? id]}]
  (let [todo (t/make-todo title)]
    (->> (if complete? (t/mark-complete todo) todo)
         (assoc-id id)
         (uc/put! create-todo)
         uc/take!!
         action->response)))


(defn respond
  "Handles a request to create a new todo"
  [{:keys [body]}]
  (let [payload (s/conform ::ts/create-request body)]
    (if (= payload ::s/invalid)
      bad-request
      (create payload))))
