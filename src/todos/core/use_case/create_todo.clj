(ns todos.core.use-case.create-todo
  (:require [clojure.core.async :refer [go-loop <! >!]]
            [todos.core.entity :as entity]
            [todos.core.entity.todo :as todo]
            [todos.core.use-case :as uc]
            [todos.core.action :as action]))


(defn create-todo
  [{:keys [in out storage] :as dependencies}]
  (let [use-case (uc/make-use-case in out)]
    (go-loop []
      (let [entity (<! in)]
        (->> entity
             (todo/save storage)
             (uc/result->action :todo/create)
             (>! out)))
      (recur))
    use-case))
