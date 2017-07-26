(ns todos.core.entities.todo
  (:require [clojure.spec.alpha :as s])
  (:import (java.util Date
                      UUID)))

(s/def ::id uuid?)
(s/def ::title string?)
(s/def ::complete? boolean?)
(s/def ::created-at inst?)
(s/def ::modified-at inst?)
(s/def ::todo (s/keys :req [::id ::title ::complete? ::created-at ::modified-at]))

(s/def ::storage-error  keyword?)
(s/def ::storage-result (s/or :todo  ::todo
                          :error ::storage-error))


(defn storage-error?
  "Check if the given storage result was an error"
  [result]
  (s/valid? ::storage-error result))


(s/fdef storage-error?
  :args (s/cat :result ::storage-result)
  :ret  boolean?)


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


(s/fdef make-todo
  :args (s/cat :id ::id :title ::title)
  :ret ::todo)


(defn complete?
  "Check if the given todo is complete"
  [todo]
  (::complete? todo))


(s/fdef complete?
  :args (s/cat :todo ::todo)
  :ret boolean?)


(defn mark-complete
  "Marks a todo as complete"
  [todo]
  (if (complete? todo)
    todo
    (merge todo {::complete? true
                 ::modified-at (Date.)})))


(s/fdef mark-complete
  :args (s/cat :todo ::todo)
  :ret ::todo)


(defprotocol TodoStorage
  (-fetch [this id] "Get a todo by id")
  (-save [this todo] "Save a todo"))


(s/def ::storage #(satisfies? TodoStorage %))


(defn fetch
  "Fetch a todo from storage"
  [storage id]
  (-fetch storage id))


(s/fdef fetch
  :args (s/cat :storage ::storage :id ::id)
  :ret  ::storage-result)


(defn save
  "Save a todo to storage"
  [storage todo]
  (-save storage todo))


(s/fdef save
  :args (s/cat :storage ::storage :todo ::todo)
  :ret  ::storage-result)


(defn insert
  "Inserts a new todo into storage"
  [storage todo]
  (let [result (fetch storage (::id todo))]
    (if (s/valid? ::todo result)
      :todo/exists
      (save storage todo))))


(s/fdef insert
  :args (s/cat :storage ::storage :todo ::todo)
  :ret  ::storage-result)
