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

  (testing "zengarden.core/css call - nested structures with nested contexts"

    (let [compare0 "  html{ display : flex; height : 100%; }  html body{ display : flex; }  html body .herclass{ float : left; } html body .hisclass{ float : left; }   html body .herclass .thisclass, html body .hisclass .thisclass{ color : white; }   html body .herclass .thisclass #anid, html body .hisclass .thisclass #anid{ color : blue; }"

          i0 [[:html {:height "100%", :display "flex"}
               [:body {:display "flex"}
                [:.herclass :.hisclass {:float "left"}
                 [:.thisclass {:color "white"}
                  [:#anid {:color "blue"}]]]]]]

          r0 (zc/css i0 false)]

      (is (= compare0 r0)))))
