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

      (is (= compare0 r0))))

  (testing "rawcss"

    (let [i0 [[:html {:height "100%", :display "flex"}] [:rawcss "svg:not(:root) {\n  overflow: hidden;\n}"]]

          r0 (zc/css (:m styles))
          c0 "\n\nhtml{ \n  height : 100%; \n  display : flex; }\nsvg:not(:root) {\n  overflow: hidden;\n}"]

      (is (= r0 c0))))

  (testing "pseudoclasses"

    (let [i0 [[:.flex-item:nth-child  {:-webkit-box-ordinal-group "1"
                                       :-moz-box-ordinal-group "1"
                                       :-webkit-order "0"
                                       :-ms-flex-order "0"
                                       :order "0"
                                       :-webkit-box-flex "0"
                                       :-moz-box-flex "0"
                                       :-webkit-flex "0 1 auto"
                                       :-ms-flex "0 1 auto"
                                       :flex "1 1 auto"
                                       :-webkit-align-self "stretch"
                                       :-ms-flex-item-align "stretch"
                                       :align-self "stretch"}]]
          r0 (zc/css i0)
          c0 "\n\n.flex-item:nth-child{ \n  -webkit-flex : 0 1 auto; \n  align-self : stretch; \n  -webkit-box-ordinal-group : 1; \n  flex : 1 1 auto; \n  -webkit-box-flex : 0; \n  -webkit-align-self : stretch; \n  -ms-flex-item-align : stretch; \n  -moz-box-ordinal-group : 1; \n  -ms-flex-order : 0; \n  order : 0; \n  -moz-box-flex : 0; \n  -webkit-order : 0; \n  -ms-flex : 0 1 auto; }"]

      (is (= r0 c0)))))
