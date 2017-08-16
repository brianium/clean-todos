(ns todos.delivery.web.events
  (:require [re-frame.core :as rf]
            [todos.delivery.web.list-todos.events]
            [todos.delivery.web.create-todo.events]
            [todos.delivery.web.toggle-todo.events]
            [todos.delivery.web.delete-todos.events]
            [todos.delivery.web.edit-title.events]))


(rf/reg-event-fx
  :initialize
  (fn [{:keys [db]} _]
    {:db       {:todos         (array-map)
                :ui            {:show-spinner :false}}
     :dispatch [:list-todos]}))


(rf/reg-event-db
  :set-showing
  (fn [db [_ status]]
    (assoc db :status status)))
