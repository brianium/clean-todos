(ns todos.core.use-cases.create-todo-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [clojure.test.check] ;; https://github.com/clojure-emacs/cider/issues/1841#issuecomment-266072462
            [clojure.core.async :refer [chan >! <! go]]
            [todos.core.use-cases.create-todo :as ct]
            [todos.core.use-case :as uc]
            [todos.core.entities.todo :as todo]
            [todos.core.action :as action]
            [todos.storage.todo.collection :refer [make-storage]]
            [todos.test :refer [test-async]]))


(defn- create-deps
  ([in out storage]
   {:in      in
    :out     out
    :storage storage})
  ([in out]
   (create-deps in out (make-storage))))


(deftest test-create-todo
  (testing "create new todo"
    (let [in       (chan)
          out      (chan)
          deps     (create-deps in out)
          use-case (ct/create-todo deps)
          entity   (todo/make-todo "Not done")]
      (uc/put! use-case entity)
      (test-async
        (go (uc/take! use-case (fn [action]
                                 (is (= :todo/created (::action/type action)))))))))
  (testing "creating existing todo"
    (let [in       (chan)
          out      (chan)
          entity   (todo/make-todo "New todo")
          deps     (create-deps in out (make-storage #{entity}))
          use-case (ct/create-todo deps)]
      (uc/put! use-case entity)
      (test-async
        (go (uc/take! use-case (fn [action]
                                 (is (= :todo/exists (::action/type action)))
                                 (is (true? (::action/error? action))))))))))


(defn deps-gen [] (s/gen #{(create-deps (chan) (chan))}))
(def gen-overrides {::ct/dependencies deps-gen})


(deftest generated-tests
  (doseq [test-output (-> (st/enumerate-namespace 'todos.core.use-cases.create-todo)
                          (st/check {:gen gen-overrides}))]
    (testing (-> test-output :sym name)
      (is (true? (-> test-output :clojure.spec.test.check/ret :result))))))
