(ns todos.core.use-case.update-todo
  (:require [clojure.core.async :refer [go-loop <! >!]]
            [todos.core.entity :as entity]
            [todos.core.entity.todo :as todo]
            [todos.core.use-case :as uc]))


(defn- strip-nils
  "Removes nils from a todo"
  [todo]
  (into {} (filter (comp some? val) todo)))


(defn update-todo
  [{:keys [in out storage] :as dependencies}]
  (let [use-case (uc/make-use-case in out)]
    (go-loop []
      (let [[id todo] (<! in)
            current   (todo/fetch storage id)]
        (if (entity/storage-error? current)
          (>! out (uc/result->action :todo/update current))
          (->> {::entity/id id}
               (merge todo)
               strip-nils
               (merge current)
               (todo/save storage)
               (uc/result->action :todo/update)
               (>! out))))
      (recur))
    use-case))
