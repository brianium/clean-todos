(ns todos.delivery.cli.delete
  (:require [io.aviso.ansi :refer [green red]]
            [todos.delivery.use-cases :refer [delete-todo]]
            [todos.core.use-case :as uc]
            [todos.core.action :as action]))


(defn action->exit
  [{{:keys [result]} ::action/payload}]
  (if result
    {:exit-message (green "Whew! Dodged that bullet!") :ok? true}
    {:exit-message (red "Dannnng. Something went wrong")}))


(defn exit
  "Outputs a message and returns an exit code"
  [{:keys [exit-message ok?]}]
  (println exit-message)
  (if ok? 0 1))


(defn execute
  "Handles executing the delete use case via the cli"
  [[id] _]
  (->> id
    (uc/put! delete-todo)
    (uc/take!!)
    action->exit
    exit))
