(ns zengarden.test.core
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [zengarden.core :as zc]
            [taoensso.timbre :as timbre]))


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

  ;; output to file

  ;; output inline style

  ;; output pretty-printed style

  ;; output single element
  (testing "single element"

    (is (= 1 1))))