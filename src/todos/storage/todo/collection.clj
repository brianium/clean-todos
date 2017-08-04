(ns todos.storage.todo.collection
  "A naive collection backed implementation of todo storage."
  (:require [todos.core.entity :as entity]
            [todos.core.entity.todo :as todo]))


(defn- find-todo
  "Naive lookup of a todo by id"
  [todos id]
  (let [entity (first
                 (filter #(.equals id (::entity/id %)) todos))]
    (or entity :not-found)))


(defn- update-todo
  [*todos next prev]
  (let [updated (merge prev next)]
    (-> @*todos
      (disj prev)
      (conj updated)
      (as-> newset (reset! *todos newset)))
    updated))


(defn- insert
  [*todos todo]
  (swap! *todos conj todo)
  todo)


(defn- save
  [*todos {:keys [::entity/id] :as todo}]
  (let [current (find-todo @*todos id)]
    (if (= current :not-found)
      (insert *todos todo)
      (update-todo *todos todo current))))


(defn- remove
  "Removes a todo by id from the collection"
  [*coll id]
  (let [current (find-todo @*coll id)]
    (if (not (entity/storage-error? current))
      (-> @*coll
        (disj current)
        (as-> updated (reset! *coll updated))
        (as-> reset (reset current))
        nil?)
      false)))


(defrecord CollectionStorage [*coll]
  todo/TodoStorage
  (-fetch [_ id] (find-todo @*coll id))
  (-save  [_ todo] (save *coll todo))
  (-all [_] @*coll)
  (-remove [_ id] (remove *coll id)))


(defn make-storage
  ([coll]
   (->CollectionStorage (atom (set coll))))
  ([]
   (make-storage #{})))
