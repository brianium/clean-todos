(ns todos.delivery.cli.use-cases
  (:require [mount.core :refer [defstate]]
            [clojure.core.async :as async]
            [todos.core.use-case :as uc]
            [todos.core.use-cases.create-todo :as ct]
            [todos.delivery.cli.storage :refer [store]]))


(defstate create-todo :start (ct/create-todo {:in      (async/chan)
                                              :out     (async/chan)
                                              :storage store})
                      :stop (fn []
                              (doseq [chan [(uc/input create-todo) (uc/output create-todo)]]
                                (async/close! chan))))
