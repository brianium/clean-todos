(ns todos.core.use-cases.create-todo
  (:require [clojure.core.async :refer [go-loop <! >!]]
            [clojure.spec.alpha :as s]
            [todos.core.entities.todo :as todo]
            [todos.core.use-case :as uc]
            [todos.core.action :as action]))


(s/def ::in           ::uc/read-port)
(s/def ::out          ::uc/write-port)
(s/def ::storage      ::todo/storage)
(s/def ::dependencies (s/keys :req-un [::in ::out ::storage]))


(defn result->action
  "Creates an action for the result of creating a new todo"
  [result]
  (if (todo/storage-error? result)
    (action/make-error result)
    (action/make-action :todo/created {:result result})))


(s/fdef result->action
  :args  (s/cat :result ::todo/storage-result)
  :ret   ::action/action)


(defn create-todo
  [{:keys [in out storage] :as dependencies}]
  (let [use-case (uc/make-use-case in out)]
    (go-loop []
      (let [entity (<! in)]
        (->> entity
             (todo/insert storage)
             (result->action)
             (>! out)))
      (recur))
    use-case))


(s/fdef create-todo
  :args (s/cat :dependencies ::dependencies)
  :ret  ::uc/use-case)
