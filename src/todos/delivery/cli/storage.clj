(ns todos.delivery.cli.storage
  (:require [mount.core :refer [defstate]]
            [todos.storage.todo.collection :refer [make-storage]]))


(defstate store :start (make-storage))
