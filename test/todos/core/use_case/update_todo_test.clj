(ns todos.core.use-case.update-todo-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [clojure.test.check] ;; https://github.com/clojure-emacs/cider/issues/1841#issuecomment-266072462
            [todos.core.use-case.dependencies :as deps]))


(deftest generated-tests
  (doseq [test-output (st/check
                        (st/enumerate-namespace 'todos.core.use-case.update-todo)
                        {:gen deps/gen-overrides})]
    (testing (-> test-output :sym name)
      (is (true? (-> test-output :clojure.spec.test.check/ret :result))))))
