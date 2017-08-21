(ns todos.core.use-case-test
  (:require [clojure.test :refer :all]
            [todos.core.use-case :as use-case]
            [todos.core.action :as action]))


(deftest test-result->action
  (testing "storage result is an error"
    (let [action (use-case/result->action :my/type :error!!)]
      (is (true? (::action/error? action)))))
  (testing "storage result is not an error"
    (let [action (use-case/result->action :my/type {:id "so-unique"})]
      (is (false? (::action/error? action))))))
