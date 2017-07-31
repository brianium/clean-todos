(ns todos.core.use-case.list-todos-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [clojure.core.async :refer [chan go]]
            [clojure.test.check] ;; https://github.com/clojure-emacs/cider/issues/1841#issuecomment-266072462
            [todos.core.entity.todo :as todo]
            [todos.core.use-case :as uc]
            [todos.core.action :as action]
            [todos.storage.todo.collection :refer [make-storage]]
            [todos.core.use-case.list-todos :as lt]
            [todos.test :refer [test-async]]
            [todos.core.use-case.dependencies :as deps]))


(deftest test-list-todos
  (let [in           (chan)
        out          (chan)
        active       (todo/make-todo "Active")
        complete     (-> (todo/make-todo "Complete") todo/mark-complete)
        storage      (make-storage #{complete active})
        dependencies (deps/create-deps in out storage)]
    (testing "filtering complete todos"
      (let [list-todos (lt/list-todos dependencies)]
        (uc/put! list-todos :completed)
        (test-async
          (go (uc/take! list-todos
                (fn [{{:keys [result]} ::action/payload}]
                  (is (= 1 (count result)))
                  (is (= "Complete" (-> (first result) ::todo/title)))))))))
    (testing "filtering active todos"
      (let [list-todos (lt/list-todos dependencies)]
        (uc/put! list-todos :active)
        (test-async
          (go (uc/take! list-todos
                (fn [{{:keys [result]} ::action/payload}]
                  (is (= 1 (count result)))
                  (is (= "Active" (-> (first result) ::todo/title)))))))))
    (testing "listing all todos"
      (let [list-todos (lt/list-todos dependencies)]
        (uc/put! list-todos :all)
        (test-async
          (go (uc/take! list-todos
                (fn [{{:keys [result]} ::action/payload}]
                  (is (= 2 (count result)))))))))))



(deftest generated-tests
  (doseq [test-output (-> (st/enumerate-namespace 'todos.core.use-case.list-todos)
                          (st/check {:gen deps/gen-overrides}))]
    (testing (-> test-output :sym name)
      (is (true? (-> test-output :clojure.spec.test.check/ret :result))))))
