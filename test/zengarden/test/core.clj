(ns zengarden.test.core
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [zengarden.core :refer :all]
            [taoensso.timbre :as timbre]))

#_(defn fixture-http-handler [f]
  (timbre/debug "[FIXTURE] fixture-http-handler")
  (f))

#_(use-fixtures :each fixture-http-handler)


(deftest core

  (testing ""

    (is (= 1 1))))
