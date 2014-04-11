(ns zengarden.one
  (:require [clojure.zip :as zip]))


(declare walka walkb)

(def u [[:html {:height "100%" :display "flex"}
         [:body {:display "flex"}
          [:.myclass {:float "left"}]]
         [:footer {:background-color "black"}]]])

(def v [[:html {:height "100%" :display "flex"}
         [:body {:display "flex"}
          [:.herclass :.hisclass {:float "left"}
           [:.thisclass {:color "white"}
            [:#anid {:color "blue"}]]]]]])

(def w [[:html {:height "100%" :display "flex"}
         [:body {:display "flex"}]]])

(def x [[:html {:height "100%" :display "flex"}]])

(def y [[:.herclass :.hisclass :#anid {:margin "0 0 0 0"}]])


(defn walka [clist context]

  (loop [cl clist ctx context]

    (let [node (first cl)
          remaining (rest cl)]

      (println "node[" node "] / context[" ctx "]")
      (walkb node ctx)
      (if-not (empty? remaining)
        (recur remaining ctx)))))


(defn walkb [node context]

  (let [elements (filter keyword? node)
        attrs (first (filter map? node))
        children (filter vector? node)]

    (println "elements[" elements "]")
    (println "attributes[" attrs "]")
    (println "children[" children "]")
    (loop [elems elements]
      (let [eelem (first elems)
            relem (rest elems)]
        (println "... each element[" eelem "] / context[" context "]")
        (if-not (empty? relem)
          (recur relem))))
    (println)

    (if-not (empty? children)
      (walka children (if (= 1 (count elements))
                        (concat context elements)
                        (conj (into [] context)
                              (into [] elements)))))))
