(ns zengarden.walk
  (:require [clojure.string :as str]
            [schema.core :as s]
            [taoensso.timbre :as timbre]

            [zengarden.process :as zp]
            [zengarden.util :as zu]))

(zu/turn-on-validation)
(declare walka walkb)

(defn filter-pseudoclass-brackets [elements]

  (->> elements
       (remove list?)
       (map (fn [e]

              (let [eS (name e)]
                (timbre/debug "... " eS)
                (if (re-find #":" eS)
                  (keyword (first (str/split eS #":")))
                  e))))))

(defn dispatch-element [node context pretty]

  (let [elements (filter #(or (keyword? %)
                              (list? %)) node)
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
                                              (if (keyword? eelem) (if pretty (with-out-str (newline)) " "))
                                              (if (keyword? eelem)    ;; deal with element brackets
                                                (zp/process-element eelem (into [] context))
                                                (zp/process-element-brackets eelem (into [] context)))
                                              (if (and (not (list? (first relem)))
                                                       (not (nil? attrs)))  ;; ensure element, lookahead 1
                                                (zp/process-attributes attrs pretty)
                                                "{}"))]

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
                    (concat context (filter-pseudoclass-brackets elements))
                    (conj (into [] context)
                          (into [] (filter-pseudoclass-brackets elements))))
                  pretty
                  element-string)))))

(defn dispatch-import [node context pretty] (timbre/debug "dispatch-import CALLED[" node "]")

  (zp/process-import node))

(defn dispatch-media [node context pretty]

  (let [media-head (zp/process-media-head node)

        rules (->> node (filter vector?) first)
        media-tail (if rules
                     (dispatch-element rules [] pretty)
                     nil)]

    (str media-head

         (if media-tail
           (str
            " {"
            (if pretty (with-out-str (newline)))
            media-tail
            (if pretty (with-out-str (newline)))
            "}")

           " {}"))))

(defn dispatch-charset [node context pretty] (timbre/debug "dispatch-charset CALLED[" node "]"))
(defn dispatch-supports [node context pretty] (timbre/debug "dispatch-supports CALLED[" node "]"))

(defn dispatch-namespace [node context pretty]

  (timbre/debug "dispatch-namespace CALLED[" node "]")
  (zp/process-namespace node pretty))


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
     (cond
      (= :at-import (first node)) (dispatch-import node context pretty)
      (= :at-media (first node)) (dispatch-media node context pretty)
      (= :at-charset (first node)) (dispatch-charset node context pretty)
      (= :at-supports (first node)) (dispatch-supports node context pretty)
      (= :at-namespace (first node)) (dispatch-namespace node context pretty)
      (= :rawcss (first node)) (second node)
      :else (dispatch-element node context pretty))))
