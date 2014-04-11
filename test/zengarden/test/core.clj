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


  ;; output inline style

  ;; output pretty-printed style


  ;; output single element with attributes (:x styles)

  ;; output 'n' sibling elements with attributes (:y stlyes)

  ;; output nested structure (:w styles)

  ;; output nested structure with 'n' siblings in the middle (:v styles)
)
