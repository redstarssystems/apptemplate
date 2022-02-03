(ns build.tasks
  "Babashka tasks for project"
  (:require
    [babashka.fs :as fs]
    [babashka.process :refer [process check]]
    [babashka.tasks]
    [babashka.wait :as wait]
    [build.config :as config]
    [build.init :as init]
    [clojure.string :as string]
    [taoensso.timbre :as timbre]))


;;;;;;;;;;;;;;;;;
;; Tasks only
;;;;;;;;;;;;;;;;;

(defn clean
  "Clean target folder"
  [& _]
  (fs/delete-tree config/target-folder)
  (fs/create-dir config/target-folder))


(defn build
  "Build standalone executable uberjar file"
  [& _]
  (fs/create-dirs config/target-folder)
  (babashka.tasks/shell {:extra-env (config/get-project-env)} (str "clojure -T:build uberjar")))


(defn run
  "Run application (-main function)"
  [& args]
  (babashka.tasks/clojure (str "-M:run " (apply str (interpose " " args)))))


(defn install
  "Install this app"
  [& args]
  (println "This task should be implemented (stub)"))


(defn deploy
  "Deploy this app"
  [& args]
  (println "This task should be implemented (stub)"))


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
  (babashka.tasks/clojure (str "-M:outdated ")))

(defn outdated-fix
  "Check for outdated dependencies and fix"
  [& args]
  (babashka.tasks/clojure (str "-M:outdated --upgrade --force" (apply str (interpose " " args)))))


(defn format
  "Format source code"
  [& _]
  (babashka.tasks/shell "cljstyle fix"))


(defn lint
  "Lint source code"
  [& _]
  (babashka.tasks/shell "clj-kondo --parallel --lint src:test:dev/src")
  (babashka.tasks/shell "cljstyle check"))

(defn standalone
  "Create a standalone application with bundled JDK (using jlink, JDK 9+)"
  [& _]
  (if (nil? (fs/which "jlink"))
    (do
      (println "jlink is not installed. Please, install JDK jmod package.\n")
      (System/exit 1))
    (let [project-config    (config/get-project-env)
          bundle-name       (clojure.core/format "%s-%s" (:artifact-id project-config) (:artifact-version project-config))
          bundle-path       (clojure.core/format "%s/%s" (:target-folder project-config) bundle-name)
          bundle-jvm-params "-Xmx1g"
          script-body       (clojure.core/format "#!/bin/sh\n\nbin/java %s -cp .:./lib/* %s.%s"
                              bundle-jvm-params
                              (string/replace (str (:group-id project-config)) (re-pattern "-") "_") ;; Java class should not contain dashes
                              (string/replace (str (:artifact-id project-config)) (re-pattern "-") "_"))
          script-file       (clojure.core/format "%s/start.sh" bundle-path)]
      (println "Create bundle to" bundle-path)
      (fs/delete-tree bundle-path)
      (do (init/prf "Building Uberjar...") (build))
      (babashka.tasks/shell (clojure.core/format "jlink --output %s --add-modules java.sql,java.management,jdk.management,java.desktop,java.naming,jdk.unsupported --strip-debug --compress 2 --no-header-files --no-man-pages" bundle-path))
      (babashka.tasks/shell (clojure.core/format "cp %s %s/lib/" (str (:target-folder project-config) "/"
                                                                   (:artifact-id project-config) "-"
                                                                   (:artifact-version project-config)
                                                                   ".jar")
                              bundle-path))
      (spit script-file script-body)
      (babashka.tasks/shell (clojure.core/format "chmod +x %s" script-file))
      (babashka.tasks/shell (clojure.core/format "tar cvfz %s/%s-%s.tar.gz -C %s %s" (:target-folder project-config)
                              (:artifact-id project-config) (:artifact-version project-config)
                              (:target-folder project-config) bundle-name)))))


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
