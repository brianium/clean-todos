(ns todos.core.action)


(defn make-action
  "Creates a new action"
  ([type payload]
   {::type    type
    ::error?  false
    ::payload payload})
  ([type]
   {::type   type
    ::error? false}))


(defn make-error
  "Creates a new error action"
  ([type payload]
   {::type    type
    ::error?  true
    ::payload payload})
  ([type]
   {::type    type
    ::error?  true}))


(defn error?
  [action]
  (::error? action))
