(ns todos.storage.todo.collection
  "A simple collection backed implementation of todo storage"
  (:require [todos.core.entities.todo :as todo]))


(defn- find-todo
  "Naive lookup of a todo by id"
  [todos id]
  (let [entity (first
                 (filter #(.equals id (::todo/id %)) todos))]
    (if entity entity :not-found)))


(defrecord CollectionStorage [*coll]
  todo/TodoStorage
  (-fetch [_ id] (find-todo @*coll id))
  (-save  [_ todo] (do (swap! *coll conj todo)
                       todo)))


(defn make-storage
  ([coll]
   (->CollectionStorage (atom coll)))
  ([]
   (make-storage #{})))
