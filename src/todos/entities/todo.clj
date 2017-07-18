(ns todos.entities.todo
  (:require [clojure.spec.alpha :as s])
  (:import (java.util Date)))

(s/def ::title string?)
(s/def ::complete? boolean?)
(s/def ::created-at inst?)
(s/def ::modified-at inst?)
(s/def ::todo (s/keys :req [::title ::complete? ::created-at ::modified-at]))


(defn make-todo
  "Create a new incomplete todo"
  [title]
  {::title title
   ::complete? false
   ::created-at (Date.)
   ::modified-at (Date.)})


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


(s/fdef make-todo
  :args (s/cat :title ::title)
  :ret ::todo)


(s/fdef complete?
  :args (s/cat :todo ::todo)
  :ret boolean?)


(s/fdef mark-complete
  :args (s/cat :todo ::todo)
  :ret ::todo)
