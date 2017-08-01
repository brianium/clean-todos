(ns todos.delivery.cli.storage
  (:require [mount.core :refer [defstate]]
            [todos.storage.todo.sqlite :refer [make-storage make-db-spec]]))


(defstate db :start (make-db-spec (str (System/getProperty "user.home") "/.todos")))


(defstate store :start (make-storage db))
