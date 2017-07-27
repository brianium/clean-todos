(ns todos.delivery.cli.core
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [io.aviso.ansi :refer [yellow white red-bg]])
  (:gen-class))


(defn usage
  "Returns a usage summary for todos cli"
  [options-summary]
  (->> [(yellow "Usage:")
        "  todos command [options]"
        ""]
       (string/join \newline)))


(defn error-msg
  "Formats the given errors as an error string"
  [errors]
  (-> "The following errors occurred: "
      (str (string/join errors \newline))
      white
      red-bg))


(def cli-options
  [["-h" "--help"]])


(defn validate-args
  "Validate command line arguments. Either return a map indicating the program
  should exit (with a error message, and optional ok status), or a map
  indicating the action the program should take and the options provided."
  [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options)
      {:exit-message (usage summary) :ok? true}
      
      errors
      {:exit-message (error-msg errors)}

      (and (>= 1 (count arguments))
           (#{"create"} (first arguments)))
      {:action (first arguments) :options options :args (rest arguments)}

      :else
      {:exit-message (usage summary)})))


(defn exit
  "Exit with the given code and message"
  [status message]
  (println message)
  (System/exit status))


(defn -main
  [& args]
  (let [{:keys [action options args exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (case action
        "create" (println "zoooooom")))))
