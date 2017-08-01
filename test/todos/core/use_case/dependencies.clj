(ns todos.core.use-case.dependencies
  "Provides common utilities for testing use cases with dependencies"
  (:require [clojure.spec.alpha :as s]
            [clojure.core.async :refer [chan]]
            [todos.storage.todo.collection :refer [make-storage]]
            [todos.core.use-case.spec :as spec]))


(defn create-deps
  ([in out storage]
   {:in      in
    :out     out
    :storage storage})
  ([in out]
   (create-deps in out (make-storage))))


(defn deps-gen [] (s/gen #{(create-deps (chan) (chan))}))
(def gen-overrides {::spec/dependencies deps-gen})
