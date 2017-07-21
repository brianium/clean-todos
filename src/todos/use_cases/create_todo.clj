(ns todos.use-cases.create-todo
  (:require [clojure.core.async :refer [go-loop <!]]
            [clojure.spec.alpha :as s]
            [todos.entities.todo :as todo]
            [todos.use-case :as uc]))


(s/def ::in           ::uc/write-port)
(s/def ::out          ::uc/read-port)
(s/def ::storage      ::todo/storage)
(s/def ::dependencies (s/keys :req [::in ::out ::storage]))


(defn create-todo
  [{:keys [in out storage] :as dependencies}]
  (let [use-case (uc/make-use-case in out)]
    (go-loop []
      (let [entity (<! in)
            result (todo/save entity)])
      (recur))
    use-case))


(s/fdef create-todo
  :args (s/cat :dependencies ::dependencies)
  :ret  ::uc/use-case)
