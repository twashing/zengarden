(ns zengarden.core
  (:require [schema.core :as s]
            [zengarden.util :as zu]))

(zu/turn-on-validation)

(defn input-validate-fn [eachi]

  (if (empty? eachi)

    true

    (let [attribute-count (count (filter map? eachi))
          child-count (count (filter vector? eachi))]

      (and
       (vector? eachi)         ;; each record is a vector
       (some keyword? eachi)   ;; there's at least 1 keyword (element)
       (or (= 0 attribute-count)
           (= 1 attribute-count))   ;; there's 0 or 1 attributes
       (or (= 0 child-count)
           (= 1 child-count))   ;; there's 0 or 1 child vectors
       ))))

(defn css-input-predicate [input]

  (and (vector? input)
       (= (count input)      ;; input in single vector or nested vector
          (count (filter input-validate-fn input)))))

(s/defn css :- s/Str
  [input :- (s/pred css-input-predicate)]

  (println "Here...")
  "")


(s/defn process-element :- s/Str
  [element :- s/Keyword
   context :- [s/Keyword]]

  (if-not (empty? context)
    (reduce #(str %1 " " %2)
            (map name
                 (conj context element)))
    (name element)))


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
