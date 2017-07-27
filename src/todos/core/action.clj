(ns todos.core.action
  (:require [clojure.spec.alpha :as s]))


(s/def ::type    keyword?)
(s/def ::error?  boolean?)
(s/def ::payload map?)
(s/def ::action  (s/keys :req [::type ::error?]
                         :opt [::payload]))


(defn make-action
  "Creates a new action"
  ([type payload]
   {::type    type
    ::error?  false
    ::payload payload})
  ([type]
   {::type   type
    ::error? false}))


(s/fdef make-action
  :args (s/cat :type   ::type
               :payload (s/? ::payload))
  :ret  ::action)


(defn make-error
  "Creates a new error action"
  ([type payload]
   {::type    type
    ::error?  true
    ::payload payload})
  ([type]
   {::type    type
    ::error?  true}))


(s/fdef make-error
  :args (s/cat :type ::type
               :payload (s/? ::payload))
  :ret  ::action)


(defn error?
  [action]
  (::error? action))


(s/fdef error?
  :args (s/cat :action ::action)
  :ret  boolean?)
