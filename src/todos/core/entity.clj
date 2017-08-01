(ns todos.core.entity)


(defn storage-error?
  "Check if the given storage result was an error"
  [result]
  (keyword? result))
