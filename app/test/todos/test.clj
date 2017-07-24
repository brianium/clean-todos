(ns todos.test
  "Test helpers for todos"
  (:require [clojure.core.async :refer [<!!]]))

(defn test-async
  [ch]
  (<!! ch))
