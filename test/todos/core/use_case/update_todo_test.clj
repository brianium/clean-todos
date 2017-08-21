(ns todos.core.use-case.update-todo-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :as async]
            [yoose.core :as yoose]
            [todos.core.entity.todo :as todo]
            [todos.core.entity :as entity]
            [todos.core.action :as action]
            [todos.core.use-case.update-todo :as ut]
            [todos.storage.todo.collection :refer [make-storage]]
            [todos.test :refer [test-async]]))


(deftest test-update-non-existent-todo
  (let [in           (async/chan)
        out          (async/chan)
        use-case     (ut/update-todo in out {:storage (make-storage)})
        updating     (todo/make-todo "Not in collection")]
    (yoose/push! use-case [(java.util.UUID/randomUUID) updating])
    (test-async
      (async/go (yoose/pull! use-case
                  (fn [{:keys [::action/error?]}]
                    (is (true? error?))))))))


(deftest test-update-todo
  (let [in           (async/chan)
        out          (async/chan)
        updating     (todo/make-todo "Not in collection")
        storage      (make-storage #{updating})
        use-case     (ut/update-todo in out {:storage storage})]
    (yoose/push! use-case [(::entity/id updating) updating])
    (test-async
      (async/go (yoose/pull! use-case
                  (fn [{:keys [::action/error?]}]
                    (is (false? error?))))))))
