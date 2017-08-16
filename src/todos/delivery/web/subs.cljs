(ns todos.delivery.web.subs
  (:require [re-frame.core :as rf]
            [todos.core.entity.todo :as t]))


(rf/reg-sub
  :visible-todos
  (fn [{:keys [status todos]} _]
    (t/filter-todos status todos)))


(rf/reg-sub
  :todo-counts
  (fn [db _]
    (let [todos        (:todos db)
          total        (count todos)
          active       (t/filter-todos :active todos)
          active-count (count active)]
      [active-count (- total active-count)])))
