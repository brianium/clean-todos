(ns todos.core.use-case.delete-todo-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [chan go]]
            [yoose.core :as yoose]
            [todos.core.entity.todo :as todo]
            [todos.core.entity :as e]
            [todos.core.action :as action]
            [todos.storage.todo.collection :refer [make-storage]]
            [todos.core.use-case.delete-todo :as dt]
            [todos.test :refer [test-async]]))


(deftest test-deleting-a-todo
  (let [in           (chan)
        out          (chan)
        entity       (todo/make-todo "Test")
        storage      (make-storage #{entity})
        use-case     (dt/delete-todo in out {:storage storage})]
    (yoose/push! use-case (::e/id entity))
    (test-async
      (go (yoose/pull! use-case
            (fn [{{:keys [result]} ::action/payload}]
              (is (true? result))))))))


(deftest test-deleting-a-non-existent-todo
  (let [in           (chan)
        out          (chan)
        use-case     (dt/delete-todo in out {:storage (make-storage)})]
    (yoose/push! use-case (e/make-uuid))
    (test-async
      (go (yoose/pull! use-case
            (fn [{{:keys [result]} ::action/payload}]
              (is (false? result))))))))
