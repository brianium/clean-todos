(ns todos.storage.todo.sqlite
  "A sqlite powered storage for todos. Some of this would apply
  to other jdbc supported databases, but for now I cry YAGNI"
  (:require [clojure.java.jdbc :as j]
            [clojure.string :as string]
            [honeysql.core :as sql]
            [clj-time.coerce :as c]
            [todos.core.entity :as entity]
            [todos.core.entity.todo :as todo])
  (:import (java.util UUID)))


(defn- date->sql
  [date]
  (c/to-long date))


(defn- sql->date
  [date]
  (let [joda (c/from-long date)]
    (.toDate joda)))


(defn- row->todo
  [result]
  (if result
    {::entity/id        (UUID/fromString (:id result))
     ::todo/title       (:title result)
     ::todo/complete?   (if (= (:complete result) 1) true false)
     ::todo/created-at  (sql->date (:created_at result))
     ::todo/modified-at (sql->date (:modified_at result))}
    :not-found))


(defn- todo->row
  [todo]
  {:id          (::entity/id todo)
   :title       (::todo/title todo)
   :complete    (if (::todo/complete? todo) 1 0)
   :created_at  (date->sql (::todo/created-at todo))
   :modified_at (date->sql (::todo/modified-at todo))})


(defn- insert-todo
  [db todo]
  (try
    (j/insert! db :todos (todo->row todo)) todo
    (catch Exception e :not-inserted)))


(defn- fetch
  [db id]
  (->> {:select [:*]
        :from   [:todos]
        :where  [:= :id id]}
    sql/format
    (j/query db)
    first
    row->todo))


(defn- all
  [db]
  (->> {:select [:*]
        :from   [:todos]}
    sql/format
    (j/query db)
    (map row->todo)))


(defrecord SqliteStorage [db]
  todo/TodoStorage
  (-fetch [_ id] (fetch db id))
  (-save [_ todo] (insert-todo db todo))
  (-all [_] (all db)))


(def table-spec
  (j/create-table-ddl :todos
    [[:id :text "PRIMARY KEY"]
     [:title :text]
     [:complete :int]
     [:created_at :int]
     [:modified_at :int]]))


(defn- db-do-commands
  "Helper that swaps argument order of jdbc/db-do-commands"
  [commands db]
  (j/db-do-commands db commands))


(defn- create-table-if-not-exists
  "Creates a todos table if it does not exist"
  [db]
  (-> table-spec
    (string/replace "CREATE TABLE" "CREATE TABLE IF NOT EXISTS")
    (db-do-commands db)))


(defn make-db-spec
  "Creates a sqlite friendly db spec for use with jdbc"
  [file]
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     file})


(defn make-storage
  "Given a db spec - create a new Jdbc backed storage for todos"
  [db]
  (create-table-if-not-exists db)
  (->SqliteStorage db))
