(ns build.config
  "Project configuration"
  (:require
    [build.init :refer [safe-exec current-date prf current-timestamp]]))


;;;;;;;;;;;;;;;;;
;; Project config and project functions
;; Calculate all project data & constants here
;;;;;;;;;;;;;;;;;


(def group-id '{{group/id}})
(def artifact-id '{{artifact/id}})
(def artifact '{{group/id}}/{{artifact/id}})

(def main-ns '{{top/ns}}.{{main/ns}})
(def artifact-version "{{version}}")

(def target-folder "target")
(def release-branches #{"master"})
(def deployable-branches #{"master" "develop"})

;; this map can be used not only in `build.clj`
(defn get-project-env
  "Calculate project environment and config values.
  Returns map which will be used for merge with ENV vars."
  []
  (println "Ignore git errors if there is no fully initialized git repo.\n")
  (let [git-branch (safe-exec "git rev-parse --abbrev-ref HEAD")
        release?   (contains? release-branches git-branch)]
    {:target-folder       target-folder
     :release-branches    release-branches
     :deployable-branches deployable-branches
     :build-time          (current-date)
     :build-timestamp     current-timestamp
     :git-url             (safe-exec "git config --get remote.origin.url")
     :git-branch          git-branch
     :git-sha             (safe-exec "git rev-parse --short HEAD")
     :git-rev-count       (safe-exec "git rev-list HEAD --count")
     :release?            release?
     :snapshot?           (not release?)
     :deployable?         (contains? deployable-branches git-branch)
     :group-id            group-id
     :artifact-id         artifact-id
     :artifact            artifact
     :main-ns             main-ns
     :artifact-version    artifact-version}))

