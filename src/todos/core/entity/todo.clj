(ns todos.core.entity.todo
  (:import (java.util Date
                      UUID)))


(defn storage-error?
  "Check if the given storage result was an error"
  [result]
  (keyword? result))


(defn make-todo
  "Create a new incomplete todo"
  ([id title]
   {::id id
    ::title title
    ::complete? false
    ::created-at (Date.)
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


(defprotocol TodoStorage
  (-fetch [this id] "Get a todo by id")
  (-save [this todo] "Save a todo")
  (-all [this] "Return a seq of all todos"))


(defn fetch
  "Fetch a todo from storage"
  [storage id]
  (-fetch storage id))


(defn save
  "Save a todo to storage"
  [storage todo]
  (-save storage todo))


(defn all
  "Return a seq of all todos"
  [storage]
  (-all storage))


(defn insert
  "Inserts a new todo into storage"
  [storage todo]
  (let [result (fetch storage (::id todo))]
    (if (storage-error? result)
      (save storage todo)
      :todo/exists)))
