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

    (let [compare0 " html{ display : flex; height : 100%; }"
          compare1 "\nhtml{ \n  display : flex; \n  height : 100%; }"
          compare2 " .herclass{ margin : 0 0 0 0; } .hisclass{ margin : 0 0 0 0; } #anid{ margin : 0 0 0 0; }"

          i0 [:html {:height "100%" :display "flex"}]
          r0 (zw/walkb i0 [] false)

          i1 [:html {:height "100%" :display "flex"}]
          r1 (zw/walkb i1 [])

          i2 [:.herclass :.hisclass :#anid {:margin "0 0 0 0"}]
          r2 (zw/walkb i2 [] false)]

      (is (= r0 compare0))
      (is (= r1 compare1))
      (is (= r2 compare2))))

  (testing "walka - nested structures"

    (let [compare0 "  html{ display : flex; height : 100%; }  html body{ display : flex; }"
          compare1 "  html{ display : flex; height : 100%; }  html body{ display : flex; }  html body .myclass{ float : left; }  html footer{ background-color : black; }"

          i0 [[:html {:height "100%", :display "flex"}
               [:body {:display "flex"}]]]
          r0 (zw/walka i0 [] false "")

          i1 [[:html
               {:height "100%", :display "flex"}
               [:body {:display "flex"}
                [:.myclass {:float "left"}]]
               [:footer {:background-color "black"}]]]
          r1 (zw/walka i1 [] false "")]

      (is (= compare0 r0))
      (is (= compare1 r1))))

  (testing "walka - nested structures with nested contexts"

    (let [compare0 "  html{ display : flex; height : 100%; }  html body{ display : flex; }  html body .herclass{ float : left; } html body .hisclass{ float : left; }   html body .herclass .thisclass, html body .hisclass .thisclass{ color : white; }   html body .herclass .thisclass #anid, html body .hisclass .thisclass #anid{ color : blue; }"

          i0 [[:html {:height "100%", :display "flex"}
               [:body {:display "flex"}
                [:.herclass :.hisclass {:float "left"}
                 [:.thisclass {:color "white"}
                  [:#anid {:color "blue"}]]]]]]

          r0 (zw/walka i0 [] false "")]

      (is (= compare0 r0))))

  (testing "dispatch-media"

    (let [i0 [:at-media {:media-queries ["screen" "print" "and" '(:grid)]}]

          i1 [:at-media {:media-queries ["screen" "print" "and" '(:orientation "landscape")]}]

          i2 [:at-media
              {:media-queries ['(:min-width "700px") "," "handheld" "and" '(:orientation "landscape")] }]

          i3 [:at-media  {:media-queries ['(:min-width "700px") "handheld" "and" '(:orientation "landscape")] }
              [:.facet_sidebar {:display "none"}]]

          r0 (zw/dispatch-media i0 [] true)
          r1 (zw/dispatch-media i1 [] true)
          r2 (zw/dispatch-media i2 [] true)
          r3 (zw/dispatch-media i3 [] true)

          c0 "@media screen print and (grid) {}"
          c1 "@media screen print and (orientation: landscape) {}"
          c2 "@media (min-width: 700px) , handheld and (orientation: landscape) {}"
          c3 "@media (min-width: 700px) handheld and (orientation: landscape) {\n\n.facet_sidebar{ \n  display : none; }\n}"]

      (is (= r0 c0))
      (is (= r1 c1))
      (is (= r2 c2))
      (is (= r3 c3)))))
