(ns todos.core.entity-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.test.check] ;; https://github.com/clojure-emacs/cider/issues/1841#issuecomment-266072462
            [clojure.spec.test.alpha :as st]
            [todos.core.entity.spec :as spec])
  (:import (java.util UUID)))


(defn uuid-gen [] (s/gen #{(.toString (UUID/randomUUID))}))
(def gen-overrides {::spec/uuid-string uuid-gen})


(deftest generated-tests
  (doseq [test-output (st/check
                        (st/enumerate-namespace 'todos.core.entity)
                        {:gen gen-overrides})]
    (testing (-> test-output :sym name)
      (is
        (true? (-> test-output :clojure.spec.test.check/ret :result))
        (-> test-output :clojure.spec.test.check/ret :result-data str)))))
