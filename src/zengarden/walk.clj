(ns zengarden.walk
  (:require [schema.core :as s]
            [zengarden.process :as zp]
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

    (let [element-string (loop [elems elements
                                result ""]

                            (let [eelem (first elems)
                                  relem (rest elems)
                                  rslt (str result
                                            (with-out-str (newline))
                                            (zp/process-element eelem context)
                                            (zp/process-attributes attrs))]

                              (println "... each element[" eelem "] / context[" context "] / result[" rslt "]")

                              (if-not (empty? relem)
                                (recur relem rslt)
                                rslt)))])
    (println)

    (if-not (empty? children)
      (walka children (if (= 1 (count elements))
                        (concat context elements)
                        (conj (into [] context)
                              (into [] elements)))))))
