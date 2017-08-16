(ns todos.core.entity.todo.spec
  (:require [todos.core.entity.spec :as es]
            [todos.core.entity.todo :as todo]
            #?(:clj [clojure.spec.alpha :as s]
               :cljs [cljs.spec.alpha :as s])))


(s/def ::todo/title string?)
(s/def ::todo/complete? boolean?)
(s/def ::todo/created-at inst?)
(s/def ::todo/modified-at inst?)
(s/def ::todo (s/merge ::es/entity
                       (s/keys :req [::todo/title ::todo/complete? ::todo/created-at ::todo/modified-at])))
(s/def ::storage #(satisfies? todo/TodoStorage %))


(s/fdef todo/make-todo
  :args (s/cat :id ::es/id :title ::todo/title)
  :ret ::todo)


(s/fdef todo/unmap->todo
  :args (s/cat :map map?)
  :ret  ::todo)


(s/fdef todo/touch
  :args (s/cat :todo ::todo)
  :ret  ::todo)


(s/fdef todo/complete?
  :args (s/cat :todo ::todo)
  :ret boolean?)


(s/fdef todo/mark-complete
  :args (s/cat :todo ::todo)
  :ret ::todo)


(s/fdef todo/mark-active
  :args (s/cat :todo ::todo)
  :ret  ::todo)


(s/fdef todo/toggle-status
  :args (s/cat :todo ::todo)
  :ret  ::todo)


(s/fdef todo/fetch
  :args (s/cat :storage ::storage :id ::es/id)
  :ret  ::es/storage-result)


(s/fdef save
  :args (s/cat :storage ::storage :todo ::todo)
  :ret  ::es/storage-result)


(s/fdef all
  :args (s/cat :storage ::storage)
  :ret  ::es/storage-result)


(s/fdef remove
  :args (s/cat :store ::storage :id ::es/id)
  :ret  boolean?)


(s/fdef filter-todos
  :args (s/cat :status keyword? :todos (s/* ::todo))
  :ret  (s/* ::todo))
