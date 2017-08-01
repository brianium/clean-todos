(ns todos.core.action.spec
  (:require [clojure.spec.alpha :as s]
            [todos.core.action :as action]))


(s/def ::action/type    keyword?)
(s/def ::action/error?  boolean?)
(s/def ::action/payload map?)
(s/def ::action  (s/keys :req [::action/type ::action/error?]
                         :opt [::action/payload]))


(s/fdef action/make-action
  :args (s/cat :type   ::action/type
               :payload (s/? ::action/payload))
  :ret  ::action)


(s/fdef action/make-error
  :args (s/cat :type ::action/type
               :payload (s/? ::action/payload))
  :ret  ::action)


(s/fdef action/error?
  :args (s/cat :action ::action)
  :ret  boolean?)
