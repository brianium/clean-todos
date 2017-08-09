(ns todos.delivery.web.interceptors
  (:require [cljs.spec.alpha :as s]
            [re-frame.core :as rf]
            [todos.delivery.web.db :as db]
            [todos.delivery.web.json :as json]))


(defn check-and-throw
  "Throws an exception if the db doesn't match the given spec"
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))


(def check-spec-interceptor (rf/after (partial check-and-throw ::db/db)))


(def data->todos
  (rf/->interceptor
    :id :data->todos
    :before (fn [context]
              (let [event    (get-in context [:coeffects :event])
                    response (peek event)
                    data     (:data response)
                    index    (dec (count event))]
                (assoc-in context [:coeffects :event index] (json/map-data data))))))
