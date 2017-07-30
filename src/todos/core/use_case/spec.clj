(ns todos.core.use-case.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.core.async.impl.protocols :refer [ReadPort WritePort]]
            [todos.core.use-case :as use-case]))


(defn read-port?
  "Check if the given port is a read port"
  [port]
  (satisfies? ReadPort port))


(defn write-port?
  "Check if the given port is a write port"
  [port]
  (satisfies? WritePort port))


(s/def ::read-port read-port?)
(s/def ::write-port write-port?)
(s/def ::use-case use-case/use-case?)
(s/def ::channel-value (complement nil?))
(s/def ::take-handler  fn?)


(s/fdef use-case/make-use-case
  :args (s/cat :in ::write-port :out ::read-port)
  :ret ::use-case)


(s/fdef use-case/put!
  :args (s/cat :use-case ::use-case :value ::channel-value)
  :ret  ::use-case)


(s/fdef use-case/take!
  :args (s/cat :use-case ::use-case :fn1-handler ::take-handler)
  :ret  ::use-case)


(s/fdef use-case/take!!
  :args (s/cat :use-case ::use-case)
  :ret  ::channel-value)


(s/fdef use-case/input
  :args (s/cat :use-case ::use-case)
  :ret  ::write-port)


(s/fdef use-case/output
  :args (s/cat :use-case ::use-case)
  :ret  ::read-port)