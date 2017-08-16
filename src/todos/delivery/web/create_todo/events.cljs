(ns todos.delivery.web.create-todo.events
  (:require [day8.re-frame.http-fx]
            [re-frame.core :as rf]
            [ajax.core :as ajax]
            [todos.core.entity :as e]
            [todos.core.entity.todo :as t]
            [todos.delivery.web.interceptors :as intr]))


(rf/reg-event-fx
  :create-todo
  [intr/check-spec-interceptor]
  (fn [{:keys [db]} [_ title]]
    (let [new-todo (t/make-todo title)
          todos    (:todos db)]
      {:db         (merge db {:todos (assoc todos (::e/id new-todo) new-todo)
                              :ui    {:show-spinner true}})
       :http-xhrio {:method          :post
                    :uri             "http://localhost:4242/todos"
                    :params          (assoc new-todo ::e/id (str (::e/id new-todo)))
                    :format          (ajax/json-request-format)
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [:create-todo-success]}})))


(rf/reg-event-db
  :create-todo-success
  [intr/data->todos
   intr/check-spec-interceptor]
  (fn [db [_ index todo]]
    (assoc-in db [:ui :show-spinner] false)))
