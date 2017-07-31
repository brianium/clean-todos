(ns todos.core.use-case-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [clojure.test.check] ;; https://github.com/clojure-emacs/cider/issues/1841#issuecomment-266072462
            [clojure.core.async :refer [chan <!! go <! >!]]
            [todos.core.use-case :as use-case]
            [todos.core.use-case.spec :as spec]
            [todos.core.action :as action]
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


(deftest test-take!!
  (let [in  (chan)
        out (chan)
        uc  (use-case/make-use-case in out)]
    (go (>! out "hello"))
    (is (= "hello" (use-case/take!! uc)))))


(deftest test-result->action
  (testing "storage result is an error"
    (let [action (use-case/result->action :my/type :error!!)]
      (is (= true (::action/error? action)))))
  (testing "storage result is not an error"
    (let [action (use-case/result->action :my/type {:id "so-unique"})]
      (is (= false (::action/error? action))))))


(defn chan-gen [] (s/gen #{(chan)}))
(defn channel-value-gen [] (s/gen string?))
(defn use-case-gen [] (s/gen #{(use-case/make-use-case (chan) (chan))}))
(defn take-handler-gen [] (s/gen #{identity}))
(def gen-overrides {::spec/read-port     chan-gen
                    ::spec/write-port    chan-gen
                    ::spec/use-case      use-case-gen
                    ::spec/channel-value channel-value-gen
                    ::spec/take-handler  take-handler-gen})


;; ignoring take!! to prevent blocking - a unit test should cover this just fine
(def ignore #{'todos.core.use-case/take!!})


(deftest generated-tests
  (doseq [test-output (-> (st/enumerate-namespace 'todos.core.use-case)
                          (clojure.set/difference ignore)
                          (st/check {:gen gen-overrides}))
          sym (-> test-output :sym name)]
    (testing sym
      (is (true? (-> test-output :clojure.spec.test.check/ret :result))))))
