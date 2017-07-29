(ns todos.delivery.cli.create
  (:require [clojure.core.async :as async]
            [io.aviso.ansi :refer [red green]]
            [todos.core.entities.todo :as todo]
            [todos.core.use-case :as uc]
            [todos.core.action :as action]
            [todos.delivery.cli.use-cases :refer [create-todo]]))


(defn action->exit
  "Converts an action type to a map describing exit before"
  [result name]
  (if (action/error? result)
    {:exit-message (red "There was an error saving the todo")}
    {:exit-message (green (str "Successfully created new todo: " name)) :ok? true}))


(defn execute
  "Exercises the create todo use case and prints results to stdout"
  [[name] _]
  (->> name
    todo/make-todo
    (uc/put! create-todo))
  (let [result                     (uc/take!! create-todo)
        {:keys [exit-message ok?]} (action->exit result name)]
    (println exit-message)
    (if ok? 0 1)))
