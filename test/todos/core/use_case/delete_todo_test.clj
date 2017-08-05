(ns todos.core.use-case.delete-todo-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [clojure.core.async :refer [chan go]]
            [clojure.test.check] ;; https://github.com/clojure-emacs/cider/issues/1841#issuecomment-266072462
            [todos.core.entity.todo :as todo]
            [todos.core.entity :as e]
            [todos.core.use-case :as uc]
            [todos.core.action :as action]
            [todos.storage.todo.collection :refer [make-storage]]
            [todos.core.use-case.delete-todo :as dt]
            [todos.test :refer [test-async]]
            [todos.core.use-case.dependencies :as deps]))


(deftest test-deleting-a-todo
  (let [in           (chan)
        out          (chan)
        entity       (todo/make-todo "Test")
        storage      (make-storage #{entity})
        dependencies (deps/create-deps in out storage)
        use-case     (dt/delete-todo dependencies)]
    (uc/put! use-case (::e/id entity))
    (test-async
      (go (uc/take! use-case
            (fn [{{:keys [result]} ::action/payload}]
              (is (true? result))))))))


(deftest test-deleting-a-non-existent-todo
  (let [in           (chan)
        out          (chan)
        dependencies (deps/create-deps in out)
        use-case     (dt/delete-todo dependencies)]
    (uc/put! use-case (e/make-uuid))
    (test-async
      (go (uc/take! use-case
            (fn [{{:keys [result]} ::action/payload}]
              (is (false? result))))))))


(deftest generated-tests
  (doseq [test-output (st/check
                        (st/enumerate-namespace 'todos.core.use-case.delete-todo)
                        {:gen deps/gen-overrides})]
    (testing (-> test-output :sym name)
      (is (true? (-> test-output :clojure.spec.test.check/ret :result))))))
