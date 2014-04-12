(ns zengarden.test.process
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [taoensso.timbre :as timbre]

            [missing-utils.core :as mu]
            [zengarden.process :as zp]))


(def styles nil)

(defn fixture-core [f]
  (alter-var-root #'styles (constantly (mu/load-edn "style.edn")))
  (f))
(use-fixtures :once fixture-core)


(deftest process

  (testing "process-element"

    ;; these are valid inputs
    (is (= "html" (zp/process-element :html [])))
    (is (= "html body .herclass" (zp/process-element :.herclass [:html :body])))

    ;; this should fail - only keywords in context vector
    (let [e1 (try (zp/process-element :.herclass [:html "body"])
                  (catch Exception e e))]

      (is (not (nil? e1)))
      (is (= clojure.lang.ExceptionInfo (type e1)))
      (is (-> e1
              .toString
              (.startsWith "clojure.lang.ExceptionInfo: Input to process-element does not match schema:"))))

    ;; this should fail - context should be a vector
    (let [e2 (try (zp/process-element :.herclass #{:html :body})
                  (catch Exception e e))]

      (is (not (nil? e2)))
      (is (= clojure.lang.ExceptionInfo (type e2)))
      (is (-> e2
              .toString
              (.startsWith "clojure.lang.ExceptionInfo: Input to process-element does not match schema:")))))

  (testing "process-attributes"

    (is (= "{ \n  display : flex; \n  height : 100%; }"
           (zp/process-attributes {:display "flex", :height "100%"})))

    (is (= "{ \n  display : flex; \n  height : 100%; }"
           (zp/process-attributes {:display "flex", :height "100%"} true)))

    (is (= "{ display : flex; height : 100%; }"
           (zp/process-attributes {:display "flex", :height "100%"} false))))

  (testing "join-nested-context"

    (let [d1 [:html :body [:.herclass :.hisclass] :a]
          d2 [:html :body [:.herclass :.hisclass] :a :b]
          d3 [:html :body [:.herclass :.hisclass] [:a :b]]

          c1 '(" html body .herclass a" " html body .hisclass a")
          c2 '(" html body .herclass a b" " html body .hisclass a b")
          c3 '(" html body .herclass a" " html body .herclass b"
               " html body .hisclass a" " html body .hisclass b")
          r1 (zp/join-nested-contexts d1)
          r2 (zp/join-nested-contexts d2)
          r3 (zp/join-nested-contexts d3)]

      (is (= r1 c1))
      (is (= r2 c2))
      (is (= r3 c3))))

  (testing "process-namespace"

    (let [i0 [:at-namespace {:prefix "svg" :url "http://www.w3.org/2000/svg"}]
          r0 (zp/process-namespace i0 false)
          c0 "@namespace svg url(http://www.w3.org/2000/svg);"]

      (is (= c0 r0))))

  (testing "transform-query-term"

    (let [i0 '(:min-width "700px")
          i1 '(:grid)

          c0 "(min-width: 700px)"
          c1 "(grid)"

          r0 (zp/transform-query-term i0)
          r1 (zp/transform-query-term i1)]

      (is (= c0 r0))
      (is (= c1 r1))))

  (testing "process-media-query"

    (let [mq1 ["screen" "print" "and" '(:grid)]
          r1 (zp/process-media-query mq1)
          c1 ["screen" "print" "and" "(grid)"]

          mq2 ['(:min-width "700px") "," "handheld" "and" '(:orientation "landscape")]
          r2 (zp/process-media-query mq2)
          c2 ["(min-width: 700px)" "," "handheld" "and" "(orientation: landscape)"]]

      (is (= c1 r1))
      (is (= c2 r2))))

  (testing "process-media-head"

    (let [i0 [:at-media {:media-queries ["screen" "print" "and" '(:grid)]}]

          i1 [:at-media {:media-queries ["screen" "print" "and" '(:orientation "landscape")]}]

          i2 [:at-media
              {:media-queries ['(:min-width "700px") "," "handheld" "and" '(:orientation "landscape")] }]

          i3 [:at-media  {:media-queries ['(:min-width "700px") "handheld" "and" '(:orientation "landscape")] }
              [:.facet_sidebar {:display "none"}]]]

      )))
