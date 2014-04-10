(ns zengarden.one
  (:require [clojure.zip :as zip]))

(def uu [[:html {:height "100%" :display "flex"}
           [:body {:display "flex"}
            [:.myclass {:float "left"}]]
           [:footer {:background-color "black"}]]])

(def u [:html {:height "100%" :display "flex"}
        [:body {:display "flex"}
         [:.myclass {:float "left"}]]
        [:footer {:background-color "black"}]])

(def v [:html {:height "100%" :display "flex"}
        [:body {:display "flex"}
         [:.myclass {:float "left"}]]])

(def w [:html {:height "100%" :display "flex"}
        [:body {:display "flex"}]])


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


(defn walkt [tree]

  (loop [loc (zip/zipper
              vector?
              #(if (vector? (last %1)) (last %1))
              (fn [x y z] (do (println (str "x[" x "] / y[" y "]")) [x y]))
              ;;#(do (println "1[" %1 "] / 2[" %2 "] / 3[" %3 "]") (conj %1 %2))
              tree)] ;; for '(into [] %2)', putting :content list into a vector

    ;;(debug/debug-repl)
    (println "visiting node[" (zip/node loc) "] / next[" (zip/next loc) "]")
    (println "")
    (if (zip/end? loc)
      (zip/root loc)
      (recur (zip/next loc)))))

(comment

  (walkt v)

  (walka u)
  )
