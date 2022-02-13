(ns build.config
  "Project configuration"
  (:require
    [clojure.string :as string]
    [build.init :refer [safe-exec current-date-struc prf current-timestamp]]))


;;;;;;;;;;;;;;;;;
;; Project config and project functions
;; Calculate all project data & constants here
;;;;;;;;;;;;;;;;;


(def group-id '{{group/id}})
(def artifact-id '{{artifact/id}})
(def artifact '{{group/id}}/{{artifact/id}})

(def main-ns '{{top/ns}}.{{main/ns}})

(def target-folder "target")
(def compile-java-sources? false)
(def java-src-folders ["java-src"])
(def release-branches #{"master"})
(def deployable-branches #{"master" "develop"})

;; this map can be used not only in `build.clj`
(defn get-project-env
  "Calculate project environment and config values.
  Returns map which will be used for merge with ENV vars."
  []
  (let [version-prefix   (let [p (-> "VERSION_PREFIX" slurp string/trim)] (if (string/blank? p) "0" p))
        git-branch       (safe-exec "git rev-parse --abbrev-ref HEAD" false)
        git-rev-count    (safe-exec "git rev-list HEAD --count" false)
        release?         (contains? release-branches git-branch)
        version-suffix   (let [s (or git-rev-count "0")] (if release? s (str s "-SNAPSHOT")))
        artifact-version (format "%s.%s" version-prefix version-suffix)
        t                (current-timestamp)]
    {:target-folder         target-folder
     :release-branches      release-branches
     :deployable-branches   deployable-branches
     :build-time            (current-date-struc t)
     :build-timestamp       t
     :git-url               (safe-exec "git config --get remote.origin.url" false)
     :git-branch            git-branch
     :git-sha               (safe-exec "git rev-parse --short HEAD" false)
     :git-rev-count         git-rev-count
     :release?              release?
     :snapshot?             (not release?)
     :deployable?           (contains? deployable-branches git-branch)
     :group-id              group-id
     :artifact-id           artifact-id
     :artifact              artifact
     :main-ns               main-ns
     :artifact-version      artifact-version
     :compile-java-sources? compile-java-sources?
     :java-src-folders      java-src-folders}))

