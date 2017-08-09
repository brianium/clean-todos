(ns todos.delivery.web.list-todos.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [todos.delivery.web.interceptors :as intr]))


(rf/reg-event-fx
  :list-todos
  (fn [{:keys [db]} _]
    {:db         (assoc-in db [:ui :show-spinner] true)
     :http-xhrio {:method          :get
                  :uri             "http://localhost:4242/todos"
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:list-todos-success]
                  :on-failure      [:list-todos-failure]}}))

(defn reduce-todos
  ""
  [data]
  (reduce #(assoc %1 (:todos.core.entity/id %2) %2) {} data))


(rf/reg-event-fx
  :list-todos-success
  [(rf/inject-cofx :status)
   intr/data->todos
   intr/check-spec-interceptor]
  (fn [{:keys [db status]} [_ todos]]
    {:db (merge db {:todos         (reduce-todos todos)
                    :status        status
                    :ui            {:show-spinner false}})}))
