(ns todos.delivery.api.spec
  (:require [clojure.spec.alpha :as s]
            [todos.core.entity.todo :as todo]
            [todos.core.entity.spec]
            [todos.core.entity.todo.spec]))


(s/def ::todo-request (s/keys :req-un [::todo/title ::todo/complete?]))
