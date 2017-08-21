(ns todos.delivery.api.update
  (:require [clojure.spec.alpha :as s]
            [ring.util.response :refer [response]]
            [yoose.core :as yoose]
            [todos.delivery.api.spec :as ts]
            [todos.delivery.use-cases :refer [update-todo]]
            [todos.core.entity :as e]
            [todos.core.entity.todo :as t]
            [todos.core.action :as action]))


;; @todo maybe just parse the explanation from spec - copied from create for now
(def bad-request
  {:status 422
   :body   {:error "Invalid title or complete? keys"}})


(defn- action->response
  "Converts an action to a response"
  [{:keys [::action/error? ::action/payload]}]
  (let [result (:result payload)]
    (if error?
      {:status (if (= result :not-found) 404 500)
       :body   {:error result}}
      {:status 204})))


(defn- patch
  "Patches an existing todo based on the request"
  [{:keys [title complete?]} id]
  (->> { ::t/title title ::t/complete? complete? }
       (vector id)
       (yoose/push! update-todo)
       yoose/pull!!
       action->response))


(defn respond
  "Handles a request to update a todo"
  [{:keys [body]} id]
  (let [payload (s/conform ::ts/update-request body)]
    (if (= payload ::s/invalid)
      bad-request
      (patch payload id))))
