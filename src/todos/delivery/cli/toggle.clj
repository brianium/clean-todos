(ns todos.delivery.cli.toggle
  (:require [clojure.core.async :as async]
            [io.aviso.ansi :refer [red green]]
            [todos.core.entity.todo :as todo]
            [todos.core.use-case :as uc]
            [todos.core.action :as action]
            [todos.delivery.cli.use-cases :refer [update-todo]]))


(defn execute
  "Exercises the update todo use case and prints results to stdout"
  [[id] _])
