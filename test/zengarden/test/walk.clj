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

  (testing "walkb - single and multiple elements"

    (timbre/set-level! :warn)

    (let [compare0 "\nhtml{ display : flex; height : 100%; }"
          compare1 "\nhtml{ \n  display : flex; \n  height : 100%; }"
          compare2 "\n.herclass{ \n  margin : 0 0 0 0; }\n.hisclass{ \n  margin : 0 0 0 0; }\n#anid{ \n  margin : 0 0 0 0; }"

          i0 [:html {:height "100%" :display "flex"}]
          r0 (zw/walkb i0 [] false)

          i1 [:html {:height "100%" :display "flex"}]
          r1 (zw/walkb i1 [])

          i2 [:.herclass :.hisclass :#anid {:margin "0 0 0 0"}]
          r2 (zw/walkb i2 [])]

      (is (= r0 compare0))

      (is (= r1 compare1))

      (is (= r2 compare2))))

  (testing "walka - nested structures"

    (let [compare0 "\n\nhtml{ \n  display : flex; \n  height : 100%; }\n\nhtml body{ \n  display : flex; }"
          compare1 "\n\nhtml{ \n  display : flex; \n  height : 100%; }\n\nhtml body{ \n  display : flex; }\n\nhtml body .myclass{ \n  float : left; }\n\nhtml footer{ \n  background-color : black; }"

          i0 [[:html {:height "100%", :display "flex"}
               [:body {:display "flex"}]]]
          r0 (zw/walka i0 [])

          i1 [[:html
               {:height "100%", :display "flex"}
               [:body {:display "flex"}
                [:.myclass {:float "left"}]]
               [:footer {:background-color "black"}]]]
          r1 (zw/walka i1 [])]

      (timbre/warn r1)

      (is (= compare0 r0))
      (is (= compare1 r1)))))
