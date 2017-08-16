(ns todos.delivery.web.json
  "Utilities for handling json responses"
  (:require [cljs.core :refer [uuid]]
            [todos.core.entity.todo :refer [unmap->todo]]))


(defn json->unmap
  "Converts a json response to a todo map with unqualified keys"
  [{:keys [id created_at modified_at] :as json}]
  (merge json {:id          (uuid id)
               :created-at  (js/Date. created_at)
               :modified-at (js/Date. modified_at)}))


(def json->todo (comp unmap->todo json->unmap))


(defmulti map-data map?)


(defmethod map-data true [data] (json->todo data))


(defmethod map-data :default [data] (mapv json->todo data))
