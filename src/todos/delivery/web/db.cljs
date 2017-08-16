(ns todos.delivery.web.db
  (:require [cljs.spec.alpha :as s]
            [re-frame.core :as rf]
            [todos.core.entity.spec :as es]
            [todos.core.entity.todo.spec :as ts])
  (:import (goog Uri)))


(s/def ::todos (s/and
                 (s/map-of ::es/id ::ts/todo)
                 #(instance? PersistentArrayMap %)))
(s/def ::status #{:completed :active :all})
(s/def ::show-spinner boolean?)
(s/def ::ui (s/keys :req-un [::show-spinner]))
(s/def ::db (s/keys :req-un [::todos ::status ::ui]))


(def route (fnil identity "/all"))


(rf/reg-cofx
  :status
  (fn [cofx _]
    (-> (.-location js/window)
      Uri.
      .getFragment
      #{"/completed" "/active"}
      route
      (subs 1)
      keyword
      (as-> status (assoc cofx :status status)))))
