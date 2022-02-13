(ns build.config
  "Project configuration"
  (:require
    [build.init :refer [safe-exec current-date prf current-timestamp]]))


;;;;;;;;;;;;;;;;;
;; Project config and project functions
;;;;;;;;;;;;;;;;;

(def target-folder "target")
(def release-branches #{"master"})
(def deployable-branches #{"master" "develop"})


(defn get-project-env
  "Calculate project environment and config values.
  Returns map which will be used for merge with ENV vars."
  []
  (println "Ignore git errors if there is no fully initialized git repo.\n")
  (let [git-branch (safe-exec "git rev-parse --abbrev-ref HEAD")
        release? (contains? release-branches git-branch)
        t (current-timestamp)]
    {:target-folder target-folder
     :release-branches release-branches
     :deployable-branches deployable-branches
     :build-time  (current-date t)
     :build-timestamp t
     :git-url     (safe-exec "git config --get remote.origin.url")
     :git-branch  git-branch
     :git-sha     (safe-exec "git rev-parse --short HEAD")
     :git-rev-count (safe-exec "git rev-list HEAD --count")
     :release?    release?
     :snapshot?   (not release?)
     :deployable? (contains? deployable-branches git-branch)}))

