(ns {{top/ns}}.{{main/ns}}
  (:gen-class)
  (:require [com.brunobonacci.mulog :as mulog]))


(defn set-global-exception-hook
  "Catch any uncaught exceptions and print them."
  []
  (Thread/setDefaultUncaughtExceptionHandler
    (reify Thread$UncaughtExceptionHandler
      (uncaughtException
        [_ thread ex]
        (println "uncaught exception" :thread (.getName thread) :desc ex)))))


(defn -main
  "entry point to app."
  [& _]
  (set-global-exception-hook)
  (mulog/start-publisher! {:type :console})
  (mulog/log ::app-start :msg "starting...")
  (println "app started.")

  (System/exit 0))