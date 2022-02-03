(ns org.rssys.apptemplate
  (:require
    [clojure.pprint :as pprint]))


(defn data-fn
  "Example data-fn handler.
  Result is merged onto existing options data."
  [data]
  ;; returning nil means no changes to options data
  (println "data-fn returning nil")
  data
  nil)


(defn template-fn
  "Example template-fn handler.
  Result is used as the EDN for the template."
  [edn data]
  ;; must return the whole EDN hash map
  (pprint/pprint data)
  edn)
