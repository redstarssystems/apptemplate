(ns build.tasks
  "Babashka tasks for project"
  (:require
    [babashka.fs :as fs]
    [babashka.process :refer [process check]]
    [babashka.tasks]
    [babashka.wait :as wait]
    [build.config :as config]
    [build.init :as init]
    [clojure.string :as str]
    [taoensso.timbre :as timbre]))


;;;;;;;;;;;;;;;;;
;; Tasks only
;;;;;;;;;;;;;;;;;

(defn clean
  "Clean target folder"
  [& _]
  (fs/delete-tree config/target-folder)
  (fs/create-dir config/target-folder))


(defn test
  "Run project tests"
  [& args]
  (babashka.tasks/shell (str "clojure -M:test " (apply str (interpose " " args)))))


(defn repl
  "Run Clojure repl"
  [& args]
  (babashka.tasks/clojure (str "-M:repl " (apply str (interpose " " args)))))


(defn outdated
  "Check for outdated dependencies"
  [& _]
  (babashka.tasks/clojure (str "-M:outdated " (apply str (interpose " " *command-line-args*)))))


(defn format
  "Format source code"
  [& _]
  (babashka.tasks/shell "cljstyle fix"))


(defn lint
  "Lint source code"
  [& _]
  (babashka.tasks/shell "clj-kondo --parallel --lint src:test:dev/src")
  (babashka.tasks/shell "cljstyle check"))


(defn requirements
  "Install project requirements"
  [& _]
  (let [os-name (clojure.string/lower-case (System/getProperty "os.name"))]
    (case os-name
      "mac os x" (do
                   (babashka.tasks/shell "brew install --cask cljstyle")
                   (babashka.tasks/shell "brew install borkdude/brew/clj-kondo")
                   (babashka.tasks/shell (str "xattr -r -d com.apple.quarantine " (init/exec "which cljstyle"))))
      (println "Please, install manually the following tools: cljstyle, clj-kondo"))))
