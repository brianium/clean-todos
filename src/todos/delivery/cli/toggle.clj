(ns todos.delivery.cli.toggle
  (:require [clojure.core.async :as async]
            [io.aviso.ansi :refer [red green]]
            [yoose.core :as yoose]
            [todos.core.entity :as entity]
            [todos.core.entity.todo :as todo]
            [todos.core.action :as action]
            [todos.delivery.use-cases :refer [update-todo]]
            [todos.delivery.storage :refer [store]]))


(defn not-found
  "Prints a not found message and returns an error exit code"
  [id]
  (-> "Could not find todo with id "
      (str id)
      red
      println)
  1)


(defn toggle-string
  [{:keys [::todo/complete? ::todo/title]}]
  (if complete?
    (str "You did it! Congrats on completing: " title)
    (str "Back to the drawing board on: " title)))


(defn action->exit
  [{:keys [::action/error? ::action/payload]}]
  (if error?
    {:exit-message (red "Could not toggle todo status")}
    {:ok? true
     :exit-message
     (->> payload
          :result
          toggle-string
          green)}))


(defn exit
  [{:keys [ok? exit-message]}]
  (println exit-message)
  (if ok? 0 1))


(defn toggle
  [id current]
  (->> [id (todo/toggle-status current)]
       (yoose/push! update-todo)
       (yoose/pull!!)
       action->exit
       exit))


(defn execute
  "Exercises the update todo use case and prints results to stdout"
  [[id] _]
  (let [current (todo/fetch store id)]
    (if (entity/storage-error? current)
      (not-found id)
      (toggle id current))))
