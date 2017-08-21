(ns todos.delivery.cli.create
  (:require [clojure.core.async :as async]
            [io.aviso.ansi :refer [red green]]
            [yoose.core :as yoose]
            [todos.core.entity.todo :as todo]
            [todos.core.action :as action]
            [todos.delivery.use-cases :refer [create-todo]]))


(defn- action->exit
  "Converts an action type to a map describing exit before"
  [result name]
  (if (action/error? result)
    {:exit-message (red "There was an error saving the todo")}
    {:exit-message (green (str "Successfully created new todo: " name)) :ok? true}))


(defn execute
  "Exercises the create todo use case and prints results to stdout"
  [[name] _]
  (let [entity                     (todo/make-todo name)
        result                     (yoose/trade!! create-todo entity)
        {:keys [exit-message ok?]} (action->exit result name)]
    (println exit-message)
    (if ok? 0 1)))
