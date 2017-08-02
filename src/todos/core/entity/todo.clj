(ns todos.core.entity.todo
  (:require [todos.core.entity :as entity])
  (:import (java.util Date
                      UUID)))


(defn make-todo
  "Create a new incomplete todo"
  ([id title]
   {::entity/id
    id
    ::title       title
    ::complete?   false
    ::created-at  (Date.)
    ::modified-at (Date.)})
  ([title]
   (make-todo (UUID/randomUUID) title)))


(defn complete?
  "Check if the given todo is complete"
  [todo]
  (::complete? todo))


(defn mark-complete
  "Marks a todo as complete"
  [todo]
  (if (complete? todo)
    todo
    (merge todo {::complete? true
                 ::modified-at (Date.)})))


(defn mark-active
  "Marks a todo as active"
  [todo]
  (if-not (complete? todo)
    todo
    (merge todo {::complete? false
                 ::modified-at (Date.)})))


(defn toggle-status
  "Toggles the status of a given todo"
  [todo]
  (if (complete? todo)
    (mark-active todo)
    (mark-complete todo)))


(defprotocol TodoStorage
  (-fetch [this id] "Get a todo by id")
  (-save [this todo] "Save a todo")
  (-insert [this todo] "Inserts a new todo")
  (-all [this] "Return a seq of all todos"))


(defn fetch
  "Fetch a todo from storage"
  [storage id]
  (-fetch storage id))


(defn save
  "Save a todo to storage. Inserts if new, otherwise updates
  an existing record"
  [storage todo]
  (-save storage todo))


(defn all
  "Return a seq of all todos"
  [storage]
  (-all storage))


(def filters
  {:completed (filter complete?)
   :active    (filter (complement complete?))})


(defn filter-todos
  [status todos]
  (if-let [xform (get filters status)]
    (transduce xform conj todos)
    todos))
