(ns zengarden.test.core
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [zengarden.core :as zc]
            [taoensso.timbre :as timbre]))

#_(defn fixture-http-handler [f]
  (timbre/debug "[FIXTURE] fixture-http-handler")
  (f))

#_(use-fixtures :each fixture-http-handler)


(deftest core

  (testing "input-validation-fn"

    (is (zc/input-validate-fn []))

    (is (not (zc/input-validate-fn [:a :b {} {}])))

    (is (zc/input-validate-fn [:a :b {} []]))

    (is (not (zc/input-validate-fn [:a :b {} [] []]))))


  ;; input in single vector or nested vector
  #_(testing "input - single vector"

    )

  ;; output to string

  ;; output to file

  ;; output inline style

  ;; output pretty-printed style

  ;; output single element
  (testing "single element"

    (is (= 1 1))))
