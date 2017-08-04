(ns todos.core.entity.todo-test
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


(deftest test-mark-active
  (testing "marking a complete todo"
    (let [entity    (todo/make-todo "Complete")
          completed (todo/mark-complete entity)
          active    (todo/mark-active completed)]
      (is (false? (::todo/complete? active)))))
  (testing "marking an active todo"
    (let [entity (todo/make-todo "Test")
          active (todo/mark-active entity)]
      (is (= entity active)))))


(deftest test-toggle-status
  (testing "toggling an incomplete todo"
    (let [entity  (todo/make-todo "test")
          toggled (todo/toggle-status entity)]
      (is (true? (::todo/complete? toggled)))))
  (testing "toggling a complete todo"
    (let [entity    (todo/make-todo "test")
          completed (todo/mark-complete entity)
          toggled   (todo/toggle-status completed)]
      (is (false? (::todo/complete? toggled))))))


(deftest test-filter-todos
  (let [complete (todo/mark-complete (todo/make-todo "Complete"))
        active   (todo/make-todo "Active")
        todos    [complete active]]
    (testing "filtering active todos"
      (let [active (todo/filter-todos :active todos)]
        (is (= 1 (count active)))
        (is (= "Active" (::todo/title (first active))))))
    (testing "filtering complete todos"
      (let [complete (todo/filter-todos :completed todos)]
        (is (= 1 (count complete)))
        (is (= "Complete" (::todo/title (first complete))))))
    (testing "defaults to all todos"
      (let [all (todo/filter-todos :unrecognized-status todos)]
        (is (= 2 (count all)))))))


(def test-storage (make-storage))
(defn storage-gen [] (s/gen #{test-storage}))
(def gen-overrides {::spec/storage storage-gen})


(deftest generated-tests
  (doseq [test-output (st/check
                        (st/enumerate-namespace 'todos.core.entity.todo)
                        {:gen gen-overrides})]
    (testing (-> test-output :sym name)
      (is
        (true? (-> test-output :clojure.spec.test.check/ret :result))
        (-> test-output :clojure.spec.test.check/ret :result-data str)))))
