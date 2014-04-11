(ns zengarden.walk
  (:require [schema.core :as s]
            [zengarden.util :as zu]))

(zu/turn-on-validation)


(declare walka walkb)

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
