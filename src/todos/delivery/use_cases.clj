(ns todos.delivery.use-cases
  "All of our deliveries will use the same dependencies - but any individual
  delivery should be able to overrwrite these with ease"
  (:require [mount.core :refer [defstate]]
            [clojure.core.async :as async]
            [yoose.core :as yoose]
            [todos.core.use-case.create-todo :as ct]
            [todos.core.use-case.list-todos :as lt]
            [todos.core.use-case.update-todo :as ut]
            [todos.core.use-case.delete-todo :as dt]
            [todos.delivery.storage :refer [store]]))


(defn- create-use-case
  [factory]
  (let [in  (async/chan 1)
        out (async/chan 1)]
    (factory in out {:storage store})))


(defstate create-todo :start (create-use-case ct/create-todo)
                      :stop  (yoose/close! create-todo))


(defstate list-todos :start (create-use-case lt/list-todos)
                     :stop  (yoose/close! list-todos))


(defstate update-todo :start (create-use-case ut/update-todo)
                      :stop  (yoose/close! update-todo))


(defstate delete-todo :start (create-use-case dt/delete-todo)
                      :stop  (yoose/close! delete-todo))
