(ns todos.delivery.api.core
  (:require [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.util.response :refer [response]]
            [compojure.core :refer [defroutes GET]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [todos.delivery.api.list :as list-todos]))


(defroutes routes
  (GET "/todos" request (list-todos/respond request))
  (route/not-found {:status 404
                    :body   {:error "Not Found"}}))


(def app
  (-> routes
      wrap-params
      (wrap-json-body {:keywords? true})
      wrap-json-response
      (wrap-defaults api-defaults)))
