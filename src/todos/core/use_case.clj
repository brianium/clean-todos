(ns todos.core.use-case
  (:require [clojure.core.async :as async :refer [go <! <!!]]))


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


(defn make-use-case
  "Creates a new use case backed by core.async"
  [in out]
  (reify UseCase
    (-put! [this value] (do (async/put! in value) this))
    (-take! [this fn1-handler] (do (go (fn1-handler (<! out))) this))
    (-take!! [_] (<!! out))
    (-input [_] in)
    (-output [_] out)))


(defn put!
  "Puts a value into the use case input port"
  [use-case value]
  (-put! use-case value)
  use-case)


(defn take!
  "Take a value from the use case output port"
  [use-case fn1-handler]
  (-take! use-case fn1-handler)
  use-case)


(defn take!!
  "Take a value from the use case output port and return it. Blocks
  if necessary"
  [use-case]
  (-take!! use-case))


(defn input
  "Get the input channel of the given use case"
  [use-case]
  (-input use-case))


(defn output
  "Get the output channel of the given use case"
  [use-case]
  (-output use-case))
