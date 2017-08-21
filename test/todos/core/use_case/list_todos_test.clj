(ns todos.core.use-case.list-todos-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [chan go]]
            [yoose.core :as yoose]
            [todos.core.entity.todo :as todo]
            [todos.core.action :as action]
            [todos.storage.todo.collection :refer [make-storage]]
            [todos.core.use-case.list-todos :as lt]
            [todos.test :refer [test-async]]))


(deftest test-list-todos
  (let [in           (chan)
        out          (chan)
        active       (todo/make-todo "Active")
        complete     (todo/mark-complete (todo/make-todo "Complete"))
        storage      (make-storage #{complete active})]
    (testing "filtering complete todos"
      (let [list-todos (lt/list-todos in out {:storage storage})]
        (yoose/push! list-todos :completed)
        (test-async
          (go (yoose/pull! list-todos
                (fn [{{:keys [result]} ::action/payload}]
                  (is (= 1 (count result)))
                  (is (= "Complete" (::todo/title (first result))))))))))
    (testing "filtering active todos"
      (let [list-todos (lt/list-todos in out {:storage storage})]
        (yoose/push! list-todos :active)
        (test-async
          (go (yoose/pull! list-todos
                (fn [{{:keys [result]} ::action/payload}]
                  (is (= 1 (count result)))
                  (is (= "Active" (::todo/title (first result))))))))))
    (testing "listing all todos"
      (let [list-todos (lt/list-todos in out {:storage storage})]
        (yoose/push! list-todos :all)
        (test-async
          (go (yoose/pull! list-todos
                (fn [{{:keys [result]} ::action/payload}]
                  (is (= 2 (count result)))))))))))
