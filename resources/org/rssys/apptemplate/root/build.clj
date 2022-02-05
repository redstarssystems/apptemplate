(ns build
  (:refer-clojure :exclude [test])
  (:require [org.corfield.build :as bb]
            [clojure.tools.build.api :as b]
            [clojure.edn]
            [clojure.pprint :as pprint]))


(def project-env {:group-id              (System/getenv "group-id")
                  :artifact-id           (System/getenv "artifact-id")
                  :artifact              (symbol (System/getenv "artifact"))
                  :main-ns               (symbol (System/getenv "main-ns"))
                  :artifact-version      (System/getenv "artifact-version")
                  :build-time            (clojure.edn/read-string (System/getenv "build-time"))
                  :target-folder         (System/getenv "target-folder")
                  :release-branches      (System/getenv "release-branches")
                  :deployable-branches   (System/getenv "deployable-branches")
                  :git-url               (System/getenv "git-url")
                  :git-branch            (System/getenv "git-branch")
                  :git-sha               (System/getenv "git-sha")
                  :git-rev-count         (System/getenv "git-rev-count")
                  :release?              (Boolean/parseBoolean (System/getenv "release?"))
                  :snapshot?             (Boolean/parseBoolean (System/getenv "snapshot?"))
                  :deployable?           (Boolean/parseBoolean (System/getenv "deployable?"))
                  :compile-java-sources? (Boolean/parseBoolean (System/getenv "compile-java-sources?"))
                  :java-src-folders      (clojure.edn/read-string (System/getenv "java-src-folders"))})

(println "Project settings:")
(clojure.pprint/pprint project-env)

(defn uberjar
  "Build the Uberjar."
  [opts]
  (-> opts
    (assoc :lib (:artifact project-env) :version (:artifact-version project-env) :main (:main-ns project-env))
    (bb/uber)))

(defn javac
  "Compile java sources"
  [_]
  (let [target-folder    (:target-folder project-env)
        class-dir        (str target-folder "/classes")
        basis            (b/create-basis {:project "deps.edn"})
        java-src-folders (:java-src-folders project-env)
        javac-opts       ["-source" "8" "-target" "8"]]
    (prn)
    (println "Compile java sources from:" (pr-str java-src-folders))
    (println "javac opts:" (pr-str javac-opts))
    (b/javac {:src-dirs   java-src-folders
              :class-dir  class-dir
              :basis      basis
              :javac-opts javac-opts})
    (println "Java sources compiled successfully to folder:" class-dir)))

