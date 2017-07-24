(ns todos.use-case-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [clojure.test.check] ;; https://github.com/clojure-emacs/cider/issues/1841#issuecomment-266072462
            [clojure.core.async :refer [chan <!! go <! >!]]
            [todos.use-case :as use-case]
            [todos.test :refer [test-async]]))


(deftest test-make-use-case
  (is (use-case/use-case? (use-case/make-use-case (chan) (chan)))))


(deftest test-put!
  (let [in  (chan)
        out (chan)
        uc  (use-case/make-use-case in out)]
    (use-case/put! uc "hello")
    (test-async
      (go (is (= "hello" (<! in)))))))


(deftest test-take!
  (let [in  (chan)
        out (chan)
        uc  (use-case/make-use-case in out)]
    (go (>! out "hello"))
    (test-async
      (go (use-case/take! uc
            (fn [x]
              (is (= x "hello"))))))))


(defn chan-gen [] (s/gen #{(chan)}))
(defn channel-value-gen [] (s/gen string?))
(defn use-case-gen [] (s/gen #{(use-case/make-use-case (chan) (chan))}))
(defn take-handler-gen [] (s/gen #{identity}))
(def gen-overrides {::use-case/read-port     chan-gen
                    ::use-case/write-port    chan-gen
                    ::use-case/use-case      use-case-gen
                    ::use-case/channel-value channel-value-gen
                    ::use-case/take-handler  take-handler-gen})


(deftest generated-tests
  (doseq [test-output (-> (st/enumerate-namespace 'todos.use-case)
                          (st/check {:gen gen-overrides}))]
    (testing (-> test-output :sym name)
      (is (true? (-> test-output :clojure.spec.test.check/ret :result))))))
