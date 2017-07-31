(ns todos.core.entity-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.test.check] ;; https://github.com/clojure-emacs/cider/issues/1841#issuecomment-266072462
            [clojure.spec.test.alpha :as st]))


(deftest generated-tests
  (doseq [test-output (st/check (st/enumerate-namespace 'todos.core.entity))]
    (testing (-> test-output :sym name)
      (is
        (true? (-> test-output :clojure.spec.test.check/ret :result))
        (-> test-output :clojure.spec.test.check/ret :result-data str)))))
