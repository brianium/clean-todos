(ns todos.core.entity.todo
  (:require [todos.core.entity :as entity]))


(defn- date
  "Creates a date in a target agnostic way"
  []
  #?(:clj (java.util.Date.)
     :cljs (js/Date.)))


(defn make-todo
  "Create a new incomplete todo"
  ([id title]
   {::entity/id   id
    ::title       title
    ::complete?   false
    ::created-at  (date)
    ::modified-at (date)})
  ([title]
   (make-todo (entity/make-uuid) title)))


(defn unmap->todo
  "Provides a way to convert a map with unqualified keys to a todo map"
  [{:keys [id title complete? created-at modified-at]
    :or   {id          (entity/make-uuid)
           title       ""
           complete?   false
           created-at  (date)
           modified-at (date)}}]
  {::entity/id   id
   ::title       title
   ::complete?   complete?
   ::created-at  created-at
   ::modified-at modified-at})


(defn touch
  "Updates modified time"
  [todo]
  (assoc todo ::modified-at (date)))


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
                 ::modified-at (date)})))


(defn mark-active
  "Marks a todo as active"
  [todo]
  (if-not (complete? todo)
    todo
    (merge todo {::complete? false
                 ::modified-at (date)})))


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
  (-all [this] "Return a seq of all todos")
  (-delete [this id] "Removes a todo from storage"))


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


(defn delete
  "Removes a todo from storage by id"
  [storage id]
  (-delete storage id))


(defn- filter-fn
  [status]
  (case status
    :active    (complement complete?)
    :completed complete?
    identity))


(defmulti filter-todos (fn [status todos] (map? todos)))


(defmethod filter-todos true
  [status todos]
  (->> todos
       (filter #((filter-fn status) (second %)))
       (into {})))


(defmethod filter-todos :default
  [status todos]
  (filter (filter-fn status) todos))
