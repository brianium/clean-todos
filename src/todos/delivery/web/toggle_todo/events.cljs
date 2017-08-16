(ns todos.delivery.web.toggle-todo.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [todos.core.entity.todo :as t]
            [todos.core.entity :as e]
            [todos.delivery.web.interceptors :as intr]))


(rf/reg-event-fx
  :toggle-todo
  [intr/check-spec-interceptor]
  (fn [{:keys [db]} [_ todo]]
    (let [todos   (:todos db)
          toggled (t/toggle-status todo)]
      {:db         (merge db {:todos (assoc todos (::e/id todo) toggled)
                              :ui    {:show-spinner true}})
       :http-xhrio {:method          :patch
                    :params          toggled
                    :uri             (str "http://localhost:4242/todos/" (::e/id todo))
                    :format          (ajax/json-request-format)
                    :response-format (ajax/raw-response-format)
                    :on-success      [:toggle-todo-success]}})))


(rf/reg-event-db
  :toggle-todo-success
  (fn [db _]
    (assoc-in db [:ui :show-spinner] false)))
