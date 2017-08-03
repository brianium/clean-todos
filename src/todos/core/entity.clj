(ns todos.core.entity
  (:import (java.util UUID)))


(defn storage-error?
  "Check if the given storage result was an error"
  [result]
  (keyword? result))


(def uuid-regexp #"(?i)^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")


(defn uuid-string?
  [str]
  (string? (re-matches uuid-regexp str)))


(defn make-uuid
  []
  (UUID/randomUUID))


(defn string->uuid
  [str]
  (UUID/fromString str))
