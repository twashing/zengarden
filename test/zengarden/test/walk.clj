(ns zengarden.test.walk
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [taoensso.timbre :as timbre]

            [missing-utils.core :as mu]
            [zengarden.walk :as zw]))


(def styles nil)

(defn fixture-core [f]
  (alter-var-root #'styles (constantly (mu/load-edn "style.edn")))
  (f))
(use-fixtures :once fixture-core)


(deftest walk

  (testing "walkb"

    (let [i1 [:html {:height "100%", :display "flex"}]
          r1 (zw/walkb i1 [])

          i2 [:.herclass :.hisclass :#anid {:margin "0 0 0 0"}]
          r2 (zw/walkb i2 [])]

      (is (= r1
             "html{
                height : 100%;
                display : flex; }"))

      (is (= r2
             ".herclass{
                margin : 0 0 0 0; }
              .hisclass{
                margin : 0 0 0 0; }
              #anid{
                margin : 0 0 0 0; }")))

    ))
