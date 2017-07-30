(ns todos.core.entities.todo.spec
  (:require [clojure.spec.alpha :as s]
            [todos.core.entities.todo :as todo]))


(s/def ::todo/id uuid?)
(s/def ::todo/title string?)
(s/def ::todo/complete? boolean?)
(s/def ::todo/created-at inst?)
(s/def ::todo/modified-at inst?)
(s/def ::todo (s/keys :req [::todo/id ::todo/title ::todo/complete? ::todo/created-at ::todo/modified-at]))
(s/def ::storage-error  keyword?)
(s/def ::storage-result (s/or :data (s/or :entity ::todo :entities (s/* ::todo))
                              :error ::storage-error))
(s/def ::storage #(satisfies? todo/TodoStorage %))


(s/fdef todo/storage-error?
  :args (s/cat :result ::storage-result)
  :ret  boolean?)


(s/fdef todo/make-todo
  :args (s/cat :id ::todo/id :title ::todo/title)
  :ret ::todo)


(s/fdef todo/complete?
  :args (s/cat :todo ::todo)
  :ret boolean?)


(s/fdef todo/mark-complete
  :args (s/cat :todo ::todo)
  :ret ::todo)


(s/fdef todo/fetch
  :args (s/cat :storage ::storage :id ::todo/id)
  :ret  ::storage-result)


(s/fdef save
  :args (s/cat :storage ::storage :todo ::todo)
  :ret  ::storage-result)


(s/fdef all
  :args (s/cat :storage ::storage)
  :ret  ::storage-result)

(s/fdef insert
  :args (s/cat :storage ::storage :todo ::todo)
  :ret  ::storage-result)
