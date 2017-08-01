(ns todos.core.action-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [clojure.test.check] ;; https://github.com/clojure-emacs/cider/issues/1841#issuecomment-266072462
            [todos.core.action :as action]))


(deftest test-make-action
  (testing "including payload"
    (let [a (action/make-action :dispatch {:hello "world"})]
      (is (= a #:todos.core.action {:type :dispatch :error? false :payload {:hello "world"}}))))
  (testing "excluding payload"
    (let [a (action/make-action :dispatch)]
      (is (= a #:todos.core.action {:type :dispatch :error? false})))))


(deftest test-make-error
  (testing "including payload"
    (let [a (action/make-error :dispatch {:hello "world"})]
      (is (= a #:todos.core.action {:type :dispatch :error? true :payload {:hello "world"}}))))
  (testing "excluding payload"
    (let [a (action/make-error :dispatch)]
      (is (= a #:todos.core.action {:type :dispatch :error? true})))))


(deftest generated-tests
  (doseq [test-output (st/check (st/enumerate-namespace 'todos.core.action))]
    (testing (-> test-output :sym name)
      (is (true? (-> test-output :clojure.spec.test.check/ret :result))))))
