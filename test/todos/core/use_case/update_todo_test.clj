(ns todos.core.use-case.update-todo-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :as async]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [clojure.test.check] ;; https://github.com/clojure-emacs/cider/issues/1841#issuecomment-266072462
            [todos.core.entity.todo :as todo]
            [todos.core.entity :as entity]
            [todos.core.use-case.dependencies :as deps]
            [todos.core.use-case :as uc]
            [todos.core.action :as action]
            [todos.core.use-case.update-todo :as ut]
            [todos.storage.todo.collection :refer [make-storage]]
            [todos.test :refer [test-async]]))


(deftest test-update-non-existent-todo
  (let [in           (async/chan)
        out          (async/chan)
        dependencies (deps/create-deps in out)
        use-case     (ut/update-todo dependencies)
        updating     (todo/make-todo "Not in collection")]
    (async/go (uc/put! use-case [(java.util.UUID/randomUUID) updating]))
    (test-async
      (async/go (uc/take! use-case
                  (fn [{:keys [::action/error?]}]
                    (is (true? error?))))))))


(deftest test-update-todo
  (let [in           (async/chan)
        out          (async/chan)
        updating     (todo/make-todo "Not in collection")
        storage      (make-storage #{updating})
        dependencies (deps/create-deps in out storage)
        use-case     (ut/update-todo dependencies)]
    (async/go (uc/put! use-case [(::entity/id updating) updating]))
    (test-async
      (async/go (uc/take! use-case
                  (fn [{:keys [::action/error?]}]
                    (is (false? error?))))))))


(deftest generated-tests
  (doseq [test-output (st/check
                        (st/enumerate-namespace 'todos.core.use-case.update-todo)
                        {:gen deps/gen-overrides})]
    (testing (-> test-output :sym name)
      (is (true? (-> test-output :clojure.spec.test.check/ret :result))))))
