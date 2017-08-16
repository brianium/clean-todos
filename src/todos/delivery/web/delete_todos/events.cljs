(ns todos.delivery.web.delete-todos.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [todos.core.entity :as e]
            [todos.core.entity.todo :as t]
            [todos.delivery.web.interceptors :as intr]))


(rf/reg-event-fx
  :delete-todo
  [intr/check-spec-interceptor]
  (fn [{:keys [db]} [_ id]]
    (let [todos (:todos db)]
      {:db         (merge db {:todos (dissoc todos id)
                              :ui    {:show-spinner true}})
       :http-xhrio {:method          :delete
                    :uri             (str "http://localhost:4242/todos/" id)
                    :format          (ajax/url-request-format)
                    :response-format (ajax/raw-response-format)
                    :on-success      [:delete-todo-success]}})))


(rf/reg-event-db
  :delete-todo-success
  (fn [db _]
    (assoc-in db [:ui :show-spinner] false)))


;;; ewwwwwwwww - I have deemed this great evil too convenient to pass up
(rf/reg-event-db
  :clear-completed
  (fn [db _]
    (let [todos (:todos db)]
      (doseq [[id todo] todos]
        (when (t/complete? todo)
          (rf/dispatch [:delete-todo id])))
      db)))
