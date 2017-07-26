(ns todos.core.use-case
  (:require [clojure.core.async :as async :refer [go <! <!!]]
            [clojure.core.async.impl.protocols :refer [ReadPort WritePort]]
            [clojure.spec.alpha :as s]))


(defprotocol UseCase
  (-put! [this value] "Place a value into the use case input port")
  (-take! [this fn1-handler] "Calls the given function with the next value taken from the output port")
  (-take!! [this] "Takes a value from the output port and returns it")
  (-input [this] "Get the input port of the use case")
  (-output [this] "Get the output port of the use case"))


(defn use-case?
  "Check if the given value is a UseCase"
  [value]
  (satisfies? UseCase value))


(defn- read-port?
  "Check if the given port is a read port"
  [port]
  (satisfies? ReadPort port))


(defn- write-port?
  "Check if the given port is a write port"
  [port]
  (satisfies? WritePort port))


(defn make-use-case
  "Creates a new use case backed by core.async"
  [in out]
  (reify UseCase
    (-put! [this value] (do (async/put! in value) this))
    (-take! [this fn1-handler] (do (go (fn1-handler (<! out))) this))
    (-take!! [_] (<!! out))
    (-input [_] in)
    (-output [_] out)))


(s/def ::read-port read-port?)
(s/def ::write-port write-port?)
(s/def ::use-case use-case?)
(s/def ::channel-value (complement nil?))
(s/def ::take-handler  fn?)

(s/fdef make-use-case
  :args (s/cat :in ::write-port :out ::read-port)
  :ret ::use-case)


(defn put!
  "Puts a value into the use case input port"
  [use-case value]
  (-put! use-case value)
  use-case)


(s/fdef put!
  :args (s/cat :use-case ::use-case :value ::channel-value)
  :ret  ::use-case)


(defn take!
  "Take a value from the use case output port"
  [use-case fn1-handler]
  (-take! use-case fn1-handler)
  use-case)


(s/fdef take!
  :args (s/cat :use-case ::use-case :fn1-handler ::take-handler)
  :ret  ::use-case)


(defn take!!
  "Take a value from the use case output port and return it. Blocks
  if necessary"
  [use-case]
  (-take!! use-case))


(s/fdef take!!
  :args (s/cat :use-case ::use-case)
  :ret  ::channel-value)


(defn input
  "Get the input channel of the given use case"
  [use-case]
  (-input use-case))


(s/fdef input
  :args (s/cat :use-case ::use-case)
  :ret  ::write-port)


(defn output
  "Get the output channel of the given use case"
  [use-case]
  (-output use-case))


(s/fdef output
  :args (s/cat :use-case ::use-case)
  :ret  ::read-port)
