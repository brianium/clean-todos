(ns todos.delivery.web.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [bidi.bidi :as b]
            [goog.events :as events]
            [todos.delivery.web.db]
            [todos.delivery.web.events]
            [todos.delivery.web.subs]
            [todos.delivery.web.views :as views])
  (:import [goog History]
           [goog.history EventType]))


(defonce routes ["/" {"completed" :completed
                      "active" :active}])


(def match (fnil identity {:handler :all}))


(defonce history
  (doto (History.)
    (events/listen EventType.NAVIGATE
      (fn [event]
        (->> (.-token event)
             (b/match-route routes)
             match
             :handler
             (vector :set-showing)
             rf/dispatch)))
    (.setEnabled true)))


(defn ^:export main
  "RUN The app!"
  []
  (rf/dispatch-sync [:initialize])
  (reagent/render [views/todo-app]
    (.getElementById js/document "app")))
