(ns todos.delivery.cli.core
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [io.aviso.ansi :as ansi]
            [mount.core :as mount]
            [todos.delivery.cli.create :as create]
            [todos.delivery.cli.list :as list-todos]
            [todos.delivery.cli.toggle :as toggle]
            [todos.delivery.cli.delete :as delete-todo])
  (:gen-class))


(defn command-usage
  "Returns a string representing individual command usage"
  [name description signature]
  (string/join
    \newline
    [(str "  " (ansi/green name) ": " description)
     (str "  " (ansi/cyan (str name " " signature)))]))


(defn usage
  "Returns a usage summary for todos cli"
  [options-summary]
  (string/join
    \newline
    [(ansi/yellow "Usage:")
     "  todos command [options]"
     ""
     (ansi/yellow "Options:")
     options-summary
     ""
     (ansi/yellow "Available Commands:")
     (command-usage "create" "Create a new todo" "todo-name")
     ""
     (command-usage "list" "List todos" "[--status STATUS]")
     ""
     (command-usage "toggle" "Toggle todo status" "todo-id")
     ""
     (command-usage "delete" "Permanently removes a todo" "todo-id")
     ""]))


(defn error-msg
  "Formats the given errors as an error string"
  [errors]
  (-> "The following errors occurred: "
      (str (string/join errors \newline))
      ansi/white
      ansi/red-bg))


(def cli-options
  [["-s" "--status STATUS" "Todo status to filter on"
    :default "all"
    :parse-fn #(or (keyword (#{"completed" "active"} %)) :all)]
   ["-h" "--help"]])


;;; The map of commands supported by the todos cli. Maps a string key to a function that
;;; receives arguments and options in that order. The function should return an exit code
(def commands
  {"create" create/execute
   "list"   list-todos/execute
   "toggle" toggle/execute
   "delete" delete-todo/execute})


(defn validate-args
  "Validate command line arguments. Either return a map indicating the program
  should exit (with a error message, and optional ok status), or a map
  indicating the action the program should take and the options provided."
  [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)
        valid-commands (-> commands keys set)]
    (cond
      (:help options)
      {:exit-message (usage summary) :ok? true}
      
      errors
      {:exit-message (error-msg errors)}

      (and (>= (count arguments) 1)
           (valid-commands (first arguments)))
      {:action (first arguments) :options options :args (rest arguments)}

      :else
      {:exit-message (usage summary)})))


(defn exit
  "Exit with the given code and message"
  ([status message]
   (println message)
   (System/exit (or status 1)))
  ([status]
   (exit status "")))


(defn- execute
  "Executes an action with arguments and options then exits"
  [action args options]
  (-> commands
    (get action)
    (apply [args options])
    (exit)))


(defn -main
  [& args]
  (mount/start)
  (let [{:keys [action options args exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (execute action args options)))
  (.addShutdownHook (Runtime/getRuntime)
    (Thread. mount/stop)))
