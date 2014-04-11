(ns zengarden.test.core
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [taoensso.timbre :as timbre]

            [missing-utils.core :as mu]
            [zengarden.core :as zc]))


(def styles nil)

(defn fixture-core [f]
  (alter-var-root #'styles (constantly (mu/load-edn "style.edn")))
  (f))
(use-fixtures :once fixture-core)


(deftest core

  (testing "input-validation-fn"

    (is (zc/input-validate-fn []))

    (is (zc/input-validate-fn [:a :s :d :f]))

    (is (zc/input-validate-fn [:b {} []]))

    (is (zc/input-validate-fn [:a :b {} []]))

    (is (not (zc/input-validate-fn [:a :b {} {}])))

    (is (not (zc/input-validate-fn [:a :b {} [] []]))))

  (testing "css-input-predicate"

    (is (zc/css-input-predicate [[:a {} []]
                                 [:b {} []]]))

    (is (not (zc/css-input-predicate [[:a {} []]
                                      [:b {} [] []]]))))

  ;; output to string
  (testing "process-element"

    (is (= "html" (zc/process-element :html [])))

    (is (= "html body .herclass" (zc/process-element :.herclass [:html :body])))

    (let [e1 (try (zc/process-element :.herclass [:html "body"])
                  (catch Exception e e))]

      (is (not (nil? e1)))
      (is (= clojure.lang.ExceptionInfo (type e1)))
      (is (-> e1
              .toString
              (.startsWith "clojure.lang.ExceptionInfo: Input to process-element does not match schema:"))))

    (let [e2 (try (zc/process-element :.herclass #{:html :body})
                  (catch Exception e e))]

      (is (not (nil? e2)))
      (is (= clojure.lang.ExceptionInfo (type e2)))
      (is (-> e2
              .toString
              (.startsWith "clojure.lang.ExceptionInfo: Input to process-element does not match schema:")))))

  ;; output to file

  ;; output inline style

  ;; output pretty-printed style

  ;; output single element
  (testing "single element"

    (is (= 1 1))))
