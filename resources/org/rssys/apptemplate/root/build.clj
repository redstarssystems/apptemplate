(ns build
  (:refer-clojure :exclude [test])
  (:require [org.corfield.build :as bb]
            [clojure.pprint :as pprint]))


(def project-env {:group-id            (System/getenv "group-id")
                  :artifact-id         (System/getenv "artifact-id")
                  :artifact            (symbol (System/getenv "artifact"))
                  :main-ns             (symbol (System/getenv "main-ns"))
                  :artifact-version    (System/getenv "artifact-version")
                  :build-time          (System/getenv "build-time")
                  :build-timestamp     (Long/parseLong (System/getenv "build-timestamp"))
                  :target-folder       (System/getenv "target-folder")
                  :release-branches    (System/getenv "release-branches")
                  :deployable-branches (System/getenv "deployable-branches")
                  :git-url             (System/getenv "git-url")
                  :git-branch          (System/getenv "git-branch")
                  :git-sha             (System/getenv "git-sha")
                  :git-rev-count       (System/getenv "git-rev-count")
                  :release?            (Boolean/parseBoolean (System/getenv "release?"))
                  :snapshot?           (Boolean/parseBoolean (System/getenv "snapshot?"))
                  :deployable?         (Boolean/parseBoolean (System/getenv "deployable?"))})

(println "Project settings:")
(clojure.pprint/pprint project-env)

(defn uberjar
  "Build the Uberjar."
  [opts]
  (-> opts
    (assoc :lib (:artifact project-env) :version (:artifact-version project-env) :main (:main-ns project-env))
    (bb/uber)))

(defn install
  "Install the JAR locally."
  [opts]
  (-> opts
    (assoc :lib (:artifact project-env) :version (:artifact-version project-env))
    (bb/install)))

(defn deploy
  "Deploy the JAR to Clojars."
  [opts]
  (-> opts
    (assoc :lib (:artifact project-env) :version (:artifact-version project-env))
    (bb/deploy)))
