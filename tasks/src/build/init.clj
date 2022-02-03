(ns build.init
  "Helper functions and internal constants for all tasks."
  (:require
    [babashka.fs :as fs]
    [babashka.process :refer [process check]]
    [babashka.wait :as wait]
    [clojure.string :as str])
  (:import
    (java.io
      File)
    (java.lang
      ProcessBuilder$Redirect)
    (java.net
      ServerSocket)
    (java.time
      Instant
      LocalDateTime
      ZoneId)
    (java.time.format
      DateTimeFormatter)))


;;;;;;;;;;;;;;;;;
;; Tasks helpers and internal task constants
;;;;;;;;;;;;;;;;;

(def ansi-green "\u001B[32m")
(def ansi-reset "\u001B[0m")
(def ansi-yellow "\u001B[33m")
(def date-formatter (DateTimeFormatter/ofPattern "yyyy-MM-dd HH:mm:ss"))
(def current-timestamp (System/currentTimeMillis))


(defn current-date
  []
  (.format
    (LocalDateTime/ofInstant
      (Instant/ofEpochMilli current-timestamp)
      (ZoneId/systemDefault))
    date-formatter))


(defn prf
  [& args]
  (println (apply format args)))


(def debug-exec? false)


(defn exec
  "Execute command and put its output to a string"
  [cmd]
  (when debug-exec?
    (prf "Execute command: '%s'" cmd))
  (-> (process cmd {:out :string}) check :out str/trim-newline))


(defn safe-exec
  "Execute command ignoring bad exit codes and put its output to a string."
  [cmd]
  (try (exec cmd)
       (catch Exception e (prf "command failed: '%s', error message: '%s'" cmd (.getMessage e)))))


(defn enter
  "This hook is executed before each task."
  [current-task-fn]
  (let [{:keys [name]} (current-task-fn)]
    (prf "%s[ ] %s %s%s" ansi-yellow name (current-date) ansi-reset)))


(defn leave
  "This hook is executed after each task."
  [current-task-fn]
  (let [{:keys [name]} (current-task-fn)]
    (prf "%s[✔]︎ %s %s%s" ansi-green name (current-date) ansi-reset)))


(defn run-bb-nrepl-server
  "Run babashka nREPL server"
  [& args]
  (let [nrepl-port (with-open [sock (ServerSocket. 0)] (.getLocalPort sock))
        cp (str/join File/pathSeparatorChar ["src" "test"])
        pb (doto (java.lang.ProcessBuilder. (into ["bb" "--nrepl-server" (str nrepl-port)
                                                   "--classpath" cp]
                                              *command-line-args*))
             (.redirectOutput ProcessBuilder$Redirect/INHERIT))
        proc (.start pb)]
    (wait/wait-for-port "localhost" nrepl-port)
    (spit ".nrepl-port" nrepl-port)
    (.deleteOnExit (File. ".nrepl-port"))
    (.waitFor proc)))

