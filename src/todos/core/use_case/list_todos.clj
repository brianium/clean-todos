(ns todos.core.use-case.list-todos
  (:require [clojure.core.async :refer [go-loop <! >!]]
            [todos.core.entity.todo :as todo]
            [todos.core.use-case :as uc]))


(defn list-todos
  [{:keys [in out storage] :as dependencies}]
  (let [use-case (uc/make-use-case in out)]
    (go-loop []
      (let [status (<! in)]
        (->> (todo/all storage)
             (todo/filter-todos status)
             (uc/result->action :todo/list)
             (>! out)))
      (recur))
    use-case))
