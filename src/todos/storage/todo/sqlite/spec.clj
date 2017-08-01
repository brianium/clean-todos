(ns todos.storage.todo.sqlite.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.java.jdbc.spec :as js]
            [todos.core.entity.todo.spec :as todo]
            [todos.storage.todo.sqlite :as sqlite]))


(s/fdef sqlite/make-db-spec
  :args (s/cat :file string?)
  :ret  ::js/db-spec-driver-manager)


(s/fdef sqlite/make-storage
  :args (s/cat :db ::js/db-spec-driver-manager)
  :ret  ::todo/storage)
