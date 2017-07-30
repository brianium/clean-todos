(ns todos.core.entities.todo-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [clojure.test.check] ;; https://github.com/clojure-emacs/cider/issues/1841#issuecomment-266072462
            [todos.core.entity.todo :as todo]
            [todos.core.entity.todo.spec :as spec]
            [todos.storage.todo.collection :refer [make-storage]]))


(deftest test-make-todo
  (let [entity (todo/make-todo "Learn computers")]
    (is (= "Learn computers" (::todo/title entity)))
    (is (false? (::todo/complete? entity)))))


(deftest test-mark-complete
  (testing "marking an incomplete todo"
    (let [entity    (todo/make-todo "Incomplete")
          completed (todo/mark-complete entity)]
      (is (true? (::todo/complete? completed)))))
  (testing "marking a complete todo"
    (let [entity          (todo/make-todo "Incomplete")
          completed       (todo/mark-complete entity)
          completed-again (todo/mark-complete completed)]
      (is (= completed completed-again)))))


(deftest test-insert-todo
  (let [entity  (todo/make-todo "Incomplete")
        storage (make-storage #{entity})]
    (testing "todo already exists"
      (is (true? (keyword? (todo/insert storage entity)))))
    (testing "todo is new"
      (let [new-todo (todo/make-todo "Also not done")]
        (is (= new-todo (todo/insert storage new-todo)))))))


(def test-storage (make-storage))
(defn storage-gen [] (s/gen #{test-storage}))
(def gen-overrides {::spec/storage storage-gen})


(deftest generated-tests
  (doseq [test-output (-> (st/enumerate-namespace 'todos.core.entity.todo)
                        (st/check {:gen gen-overrides}))]
    (testing (-> test-output :sym name)
      (is
        (true? (-> test-output :clojure.spec.test.check/ret :result))
        (-> test-output :clojure.spec.test.check/ret :result-data str)))))
