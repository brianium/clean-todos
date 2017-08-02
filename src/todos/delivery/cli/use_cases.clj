(ns todos.delivery.cli.use-cases
  (:require [mount.core :refer [defstate]]
            [clojure.core.async :as async]
            [todos.core.use-case :as uc]
            [todos.core.use-case.create-todo :as ct]
            [todos.core.use-case.list-todos :as lt]
            [todos.core.use-case.update-todo :as ut]
            [todos.delivery.cli.storage :refer [store]]))


(defn close!
  "Cleans up a use case.
  TODO - consider making this part of the use case api"
  [use-case]
  (doseq [chan [(uc/input use-case) (uc/output use-case)]]
    (async/close! chan)))


(defn create-deps
  "Creates a fresh set of dependencies for a use case"
  []
  {:in      (async/chan)
   :out     (async/chan)
   :storage store})


(defstate create-todo :start (ct/create-todo (create-deps))
                      :stop (close! create-todo))


(defstate list-todos :start (lt/list-todos (create-deps))
                     :stop (close! list-todos))


(defstate update-todo :start (ut/update-todo (create-deps))
                      :stop (close! update-todo))
