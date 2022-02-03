(ns build
  (:refer-clojure :exclude [test])
  (:require
    [clojure.pprint]
    [org.corfield.build :as bb]))


(def artifact 'org.rssys/apptemplate)
(def version "0.1.0-SNAPSHOT")


;; (def version  (format "1.0.%s" (System/getenv "git-rev-count"))) ;; alternatively, use MAJOR.MINOR.COMMITS:

(def project-env
  {:artifact            artifact
   :version             version
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


(defn test
  "Run the tests."
  [opts]
  (bb/run-tests opts))
