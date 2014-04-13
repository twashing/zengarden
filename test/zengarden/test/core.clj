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
    (is (zc/input-validate-fn [:a :b {} [] []])))

  (testing "css-input-predicate"

    (is (zc/css-input-predicate [[:a {} []]
                                 [:b {} []]]))

    (is (zc/css-input-predicate [[:a {} []]
                                 [:b {} [] []]])))

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

      (is (= r0 c0))))

  (testing "landing.edn"

    (let [i0 (mu/load-edn "landing.edn")
          r0 (zc/css i0)
          c0 "\n\nhtml{ \n  color : #222; }\nbutton{ \n  color : #222; }\ninput{ \n  color : #222; }\nselect{ \n  color : #222; }\ntextarea{ \n  color : #222; }\n\n html .thing, button .thing, input .thing, select .thing, textarea .thing{}\n\n html .thing .foo, button .thing .foo, input .thing .foo, select .thing .foo, textarea .thing .foo{}\n\n html .thing .foo .bar, button .thing .foo .bar, input .thing .foo .bar, select .thing .foo .bar, textarea .thing .foo .bar{ \n  a : s; }\n\nbody{ \n  font-size : 1em; \n  line-height : 1.4; \n  font : 16px/26px Helvetica, Helvetica Neue, Arial; }\n\nimg{ \n  vertical-align : middle; }\n\nfieldset{ \n  border : 0; \n  margin : 0; \n  padding : 0; }\n\ntextarea{ \n  resize : vertical; }\n\n.ie7{ \n  padding-top : 20px; }\n.title{ \n  padding-top : 20px; }\n\n.flex-container{ \n  -webkit-box-orient : horizontal; \n  -ms-flex-direction : column; \n  -webkit-box-align : start; \n  flex-direction : column; \n  -ms-flex-align : start; \n  border : 1px red solid; \n  -webkit-flex-wrap : nowrap; \n  display : flex; \n  -moz-box-direction : normal; \n  -webkit-justify-content : flex-start; \n  -webkit-box-pack : start; \n  -ms-flex-line-pack : start; \n  align-items : flex-start; \n  justify-content : flex-start; \n  align-content : flex-start; \n  -ms-flex-pack : start; \n  -ms-flex-wrap : nowrap; \n  -moz-box-align : start; \n  -moz-box-pack : start; \n  flex-wrap : nowrap; \n  -webkit-align-content : flex-start; \n  -webkit-box-direction : normal; \n  -moz-box-orient : horizontal; \n  -webkit-flex-direction : column; \n  -webkit-align-items : flex-start; }\n\n.flex-item{ \n  border : 1px black solid; }\n\n.flex-item:nth-child(1){ \n  -webkit-flex : 0 1 auto; \n  align-self : stretch; \n  -webkit-box-ordinal-group : 1; \n  flex : 0 1 auto; \n  -webkit-box-flex : 0; \n  -webkit-align-self : stretch; \n  -ms-flex-item-align : stretch; \n  -moz-box-ordinal-group : 1; \n  -ms-flex-order : 0; \n  order : 0; \n  -moz-box-flex : 0; \n  -webkit-order : 0; \n  -ms-flex : 0 1 auto; }\n\n.flex-item:nth-child(2){ \n  -webkit-flex : 0 1 auto; \n  align-self : stretch; \n  -webkit-box-ordinal-group : 1; \n  flex : 0 1 auto; \n  -webkit-box-flex : 0; \n  -webkit-align-self : stretch; \n  -ms-flex-item-align : stretch; \n  -moz-box-ordinal-group : 1; \n  -ms-flex-order : 0; \n  order : 0; \n  -moz-box-flex : 0; \n  -webkit-order : 0; \n  -ms-flex : 0 1 auto; }\n\n.flex-item:nth-child(3){ \n  -webkit-flex : 0 1 auto; \n  align-self : stretch; \n  -webkit-box-ordinal-group : 1; \n  flex : 1 1 auto; \n  -webkit-box-flex : 0; \n  -webkit-align-self : stretch; \n  -ms-flex-item-align : stretch; \n  -moz-box-ordinal-group : 1; \n  -ms-flex-order : 0; \n  order : 0; \n  -moz-box-flex : 0; \n  -webkit-order : 0; \n  -ms-flex : 0 1 auto; }\n@media {\n\n.flex-container{ \n  -webkit-flex-direction : row; \n  -ms-flex-direction : row; \n  flex-direction : row; }\n}\n@media {}\n@media {}\n\n.ir{ \n  background-color : transparent; \n  border : 0; \n  overflow : hidden; \n  *text-indent : -9999px; }\n\n.ir:before{ \n  content : ; \n  display : block; \n  width : 0; \n  height : 150%; }\n\n.hidden{ \n  display : none !important; \n  visibility : hidden; }\n\n.visuallyhidden{ \n  border : 0; \n  clip : rect(0 0 0 0); \n  height : 1px; \n  margin : -1px; \n  overflow : hidden; \n  padding : 0; \n  position : absolute; \n  width : 1px; }\n\n.visuallyhidden .visuallyhidden.focusable:active{ \n  clip : auto; \n  height : auto; \n  margin : 0; \n  overflow : visible; \n  position : static; \n  width : auto; }\n.visuallyhidden .visuallyhidden.focusable:focus{ \n  clip : auto; \n  height : auto; \n  margin : 0; \n  overflow : visible; \n  position : static; \n  width : auto; }\n\n.invisible{ \n  visibility : hidden; }\n\n.clearfix:before{ \n  content :  ; \n  display : table; }\n.clearfix:after{ \n  content :  ; \n  display : table; }\n\n.clearfix:after{ \n  clear : both; }\n\n.clearfix{ \n  *zoom : 1; }"]

      (is (= r0 c0))))

  (testing "bug fix - testing for simple input cases"

    (let [r0 (zc/css [[:html]])
          c0 "\n\nhtml{}"

          r1 (zc/css [[:html [:body]]])
          c1 "\n\nhtml{}\n\nhtml body{}"

          r2 (zc/css [[:html [:body {:foo "bar"}]]])
          c2 "\n\nhtml{}\n\nhtml body{ \n  foo : bar; }"]

      (is (= r0 c0))
      (is (= r1 c1))
      (is (= r2 c2))))

  (testing "bug fix - testing for nested context with multiple chidlren"

    (let [i0 [[:html
               :button
               {:color "#222"}
               [:.thing [:.foo {:a "s"}]]
               [:.one {:a "s"}]]]
          r0 (zc/css i0)
          c0 "\n\nhtml{ \n  color : #222; }\nbutton{ \n  color : #222; }\n\n html .thing, button .thing{}\n\n html .thing .foo, button .thing .foo{ \n  a : s; }\nhtml{ \n  color : #222; }\nbutton{ \n  color : #222; }\n\n html .one, button .one{ \n  a : s; }"]

      (is (= r0 c0)))))
