(ns zengarden.test.process
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [taoensso.timbre :as timbre]

            [missing-utils.core :as mu]
            [zengarden.process :as zp]))


(def styles nil)

(defn fixture-core [f]
  (alter-var-root #'styles (constantly (mu/load-edn "style.edn")))
  (f))
(use-fixtures :once fixture-core)


(deftest process

  (testing "process-element"

    ;; these are valid inputs
    (is (= "html" (zp/process-element :html [])))
    (is (= "html body .herclass" (zp/process-element :.herclass [:html :body])))

    ;; this should fail - only keywords in context vector
    (let [e1 (try (zp/process-element :.herclass [:html "body"])
                  (catch Exception e e))]

      (is (not (nil? e1)))
      (is (= clojure.lang.ExceptionInfo (type e1)))
      (is (-> e1
              .toString
              (.startsWith "clojure.lang.ExceptionInfo: Input to process-element does not match schema:"))))

    ;; this should fail - context should be a vector
    (let [e2 (try (zp/process-element :.herclass #{:html :body})
                  (catch Exception e e))]

      (is (not (nil? e2)))
      (is (= clojure.lang.ExceptionInfo (type e2)))
      (is (-> e2
              .toString
              (.startsWith "clojure.lang.ExceptionInfo: Input to process-element does not match schema:")))))

)
