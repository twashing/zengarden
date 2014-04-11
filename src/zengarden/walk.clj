(ns zengarden.walk
  (:require [schema.core :as s]
            [taoensso.timbre :as timbre]

            [zengarden.process :as zp]
            [zengarden.util :as zu]))

(zu/turn-on-validation)


(declare walka walkb)

(defn walka [clist context]

  (loop [cl clist ctx context]

    (let [node (first cl)
          remaining (rest cl)]

      (timbre/debug "node[" node "] / context[" ctx "]")
      (walkb node ctx)
      (if-not (empty? remaining)
        (recur remaining ctx)))))


(defn walkb

  ([node context]
     (walkb node context true))
  ([node context pretty]
     (let [elements (filter keyword? node)
           attrs (first (filter map? node))
           children (filter vector? node)]

       (timbre/debug "elements[" elements "]")
       (timbre/debug "attributes[" attrs "]")
       (timbre/debug "children[" children "]")

       (let [element-string (loop [elems elements
                                   result ""]

                              (let [eelem (first elems)
                                    relem (rest elems)
                                    rslt (str result
                                              (with-out-str (newline))
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

           (walka children (if (= 1 (count elements))
                             (concat context elements)
                             (conj (into [] context)
                                   (into [] elements)))))))))
