(ns todos.core.use-case.create-todo-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [clojure.test.check] ;; https://github.com/clojure-emacs/cider/issues/1841#issuecomment-266072462
            [clojure.core.async :refer [chan go]]
            [todos.core.use-case.create-todo :as ct]
            [todos.core.use-case :as uc]
            [todos.core.entity.todo :as todo]
            [todos.core.action :as action]
            [todos.storage.todo.collection :refer [make-storage]]
            [todos.test :refer [test-async]]
            [todos.core.use-case.dependencies :as deps]))


(deftest test-create-todo
  (testing "create new todo"
    (let [in       (chan)
          out      (chan)
          deps     (deps/create-deps in out)
          use-case (ct/create-todo deps)
          entity   (todo/make-todo "Not done")]
      (uc/put! use-case entity)
      (test-async
        (go (uc/take! use-case (fn [action]
                                 (is (= :todo/create (::action/type action)))))))))
  (testing "creating existing todo"
    (let [in       (chan)
          out      (chan)
          entity   (todo/make-todo "New todo")
          deps     (deps/create-deps in out (make-storage #{entity}))
          use-case (ct/create-todo deps)]
      (uc/put! use-case entity)
      (test-async
        (go (uc/take! use-case (fn [action]
                                 (is (= :todo/exists (-> action ::action/payload :result)))
                                 (is (true? (::action/error? action))))))))))


(deftest generated-tests
  (doseq [test-output (-> (st/enumerate-namespace 'todos.core.use-case.create-todo)
                          (st/check {:gen deps/gen-overrides}))]
    (testing (-> test-output :sym name)
      (is (true? (-> test-output :clojure.spec.test.check/ret :result))))))
