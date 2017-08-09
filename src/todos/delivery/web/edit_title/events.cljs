(ns todos.delivery.web.edit-title.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [todos.core.entity.todo :as t]
            [todos.core.entity :as e]
            [todos.delivery.web.interceptors :as intr]))


(rf/reg-event-fx
  :edit-title
  [intr/check-spec-interceptor]
  (fn [{:keys [db]} [_ id title]]
    (let [todos (:todos db)]
      {:db         (merge db {:todos (assoc-in todos [id ::t/title] title)
                              :ui    {:show-spinner true}})
       :http-xhrio {:method          :patch
                    :uri             (str "http://localhost:4242/todos/" id)
                    :format          (ajax/json-request-format)
                    :params          {:title title}
                    :response-format (ajax/json-response-format)
                    :on-success      [:edit-title-success]}})))


(rf/reg-event-db
  :edit-title-success
  (fn [db _]
    (assoc-in db [:ui :show-spinner] false)))
