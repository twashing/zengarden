(ns zengarden.one
  (:require [clojure.zip :as zip]))


(def u [[:html {:height "100%" :display "flex"}
         [:body {:display "flex"}
          [:.myclass {:float "left"}]]
         [:footer {:background-color "black"}]]])

(def v [[:html {:height "100%" :display "flex"}
         [:body {:display "flex"}
          [:.myclass {:float "left"}]]]])

(def w [[:html {:height "100%" :display "flex"}
         [:body {:display "flex"}]]])


(declare walka walkb)

(defn walka [clist]
  (loop [cl clist]
    (let [node (first cl)
          remaining (rest cl)]

      (println "node[" node "]")
      (walkb node)
      (if-not (empty? remaining)
        (recur remaining)))))


(defn walkb [node]

  (let [elements (filter keyword? node)
        attrs (first (filter map? node))
        children (filter vector? node)]

    (println "elements[" elements "]")
    (println "attributes[" attrs "]")
    (println "children[" children "]")
    (println)

    (if-not (empty? children)
      (walka children))))
