(ns zengarden.walk
  (:require [schema.core :as s]
            [taoensso.timbre :as timbre]

            [zengarden.process :as zp]
            [zengarden.util :as zu]))

(zu/turn-on-validation)


(defn dispatch [node]

  (cond
      (= :at-import (first node)) (timbre/debug "dispatch @import")
      (= :at-media (first node)) (timbre/debug "dispatch @media")
      (= :at-charset (first node)) (timbre/debug "dispatch @charset")
      (= :at-supports (first node)) (timbre/debug "dispatch @supports")
      (= :at-namespace (first node)) (timbre/debug "dispatch @namespace")
      :else (timbre/debug "dispatch Elements")))

(declare walka walkb)

(defn walka
  "Deal with a list of node trees"

  ([clist context]
     (walka clist context true ""))
  ([clist context pretty result]

     (loop [cl clist
            ctx context
            r1 result]

       (let [node (first cl)
             remaining (rest cl)]

         (timbre/debug "node[" node "] / context[" ctx "]")
         (let [rslt (str r1
                         (if pretty (with-out-str (newline)) " ")
                         (walkb node ctx pretty))]

           (if (empty? remaining)
             rslt
             (recur remaining ctx rslt)))))))


(defn walkb
  "Process one node (or node set) and its attributes and children"

  ([node context]
     (walkb node context true))
  ([node context pretty]
     (let [result-dispatch (dispatch node)

           elements (filter keyword? node)
           attrs (first (filter map? node))
           children (filter vector? node)]

       (timbre/debug "elements[" elements "]")
       (timbre/debug "attributes[" attrs "]")
       (timbre/debug "children[" children "]")

       (let [element-string (loop [elems elements
                                   result ""]

                              ;;(zu/turn-on-validation false)
                              (let [eelem (first elems)
                                    relem (rest elems)
                                    rslt (str result
                                              (if pretty (with-out-str (newline)) " ")
                                              (zp/process-element eelem context)
                                              (zp/process-attributes attrs pretty))]

                                (timbre/debug "... each element[" eelem
                                              "] / context[" context
                                              "] / result[" rslt "]")
                                (timbre/debug)

                                (if-not (empty? relem)
                                  (recur relem rslt)
                                  rslt)))]

         (if (empty? children)

           element-string

           (walka children
                  (if (= 1 (count elements))
                    (concat context elements)
                    (conj (into [] context)
                          (into [] elements)))
                  pretty
                  element-string))))))
