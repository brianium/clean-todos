(ns todos.core.entity)


(defn storage-error?
  "Check if the given storage result was an error"
  [result]
  (keyword? result))


(def uuid-regexp #"(?i)^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")


(defn uuid-string?
  "Check if the given string conforms to a UUID v1 - 5 format"
  [str]
  (string? (re-matches uuid-regexp str)))


(defn make-uuid
  "Creates a new uuid"
  []
  #?(:clj (java.util.UUID/randomUUID)
     :cljs (cljs.core/random-uuid)))


(defn string->uuid
  "Converts a uuid string to a uuid"
  [str]
  #?(:clj (java.util.UUID/fromString str)
     :cljs (cljs.core/uuid str)))
