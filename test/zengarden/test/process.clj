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

      (is (= c0 r0)))))
