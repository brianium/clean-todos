(ns todos.delivery.cli.list
  (:require [clojure.core.async :as async]
            [io.aviso.ansi :refer [red green yellow cyan]]
            [yoose.core :as yoose]
            [todos.core.entity.todo :as todo]
            [todos.core.entity :as entity]
            [todos.core.action :as action]
            [todos.delivery.use-cases :refer [list-todos]]))


(defn- format-todo
  [output {:keys [::todo/title ::todo/complete? ::entity/id]}]
  (str output
    "[" (green (if complete? \u2713 " ")) "]  "
    title
    " (" (cyan id) ")"
    \newline))


(defn- format-todos
  [todos]
  (if (seq todos)
    (reduce
      format-todo
      (str (yellow "Todos:") \newline)
      todos)
    (yellow "Do you even have goals? Create a new todo!")))


(defn- action->exit
  "Converts an action type to a map describing exit before"
  [{{:keys [result]} ::action/payload}]
  (if (action/error? result)
    {:exit-message (red "There was an error listing todos")}
    {:exit-message (format-todos result) :ok? true}))


(defn execute
  "Exercises the create todo use case and prints results to stdout"
  [_ {:keys [status] :or {status :all}}]
  (let [result                     (yoose/trade!! list-todos status)
        {:keys [exit-message ok?]} (action->exit result)]
    (println exit-message)
    (if ok? 0 1)))
