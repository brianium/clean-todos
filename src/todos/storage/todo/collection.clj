(ns todos.storage.todo.collection
  "A simple collection backed implementation of todo storage"
  (:require [todos.entities.todo :as todo]))


(defn- find-todo [todos id]
  "Naive lookup of a todo by id"
  (let [entity (first (filter #(.equals id (::todo/id %)) todos))]
    (if entity entity :not-found)))


(defrecord CollectionStorage [*coll]
  todo/TodoStorage
  (-fetch [_ id] (find-todo @*coll id))
  (-save  [_ todo] (first (swap! *coll conj todo))))


(defn make-storage
  ([coll]
   (->CollectionStorage (atom coll)))
  ([]
   (make-storage #{})))
