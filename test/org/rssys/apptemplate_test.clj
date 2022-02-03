(ns org.rssys.apptemplate-test
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [clojure.spec.alpha :as s]
    [clojure.test :refer [deftest testing is]]
    [org.corfield.new]
    ;; for the Specs
    [org.rssys.apptemplate]))


(deftest valid-template-test
  (testing "template.edn is valid."
    (let [template (edn/read-string (slurp (io/resource "org/rssys/apptemplate/template.edn")))]
      (is (s/valid? :org.corfield.new/template template)
        (s/explain-str :org.corfield.new/template template)))))
