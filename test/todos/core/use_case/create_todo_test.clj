(ns todos.core.use-case.create-todo-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [chan go]]
            [yoose.core :as yoose]
            [todos.core.use-case.create-todo :as ct]
            [todos.core.entity.todo :as todo]
            [todos.core.action :as action]
            [todos.storage.todo.collection :refer [make-storage]]
            [todos.test :refer [test-async]]))


(deftest test-create-todo
  (testing "create new todo"
    (let [in       (chan)
          out      (chan)
          use-case (ct/create-todo in out {:storage (make-storage)})
          entity   (todo/make-todo "Not done")]
      (yoose/push! use-case entity)
      (test-async
        (go (yoose/pull! use-case (fn [action]
                                    (is (= :todo/create (::action/type action))))))))))
