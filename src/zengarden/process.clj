(ns zengarden.process
  (:require [schema.core :as s]
            [taoensso.timbre :as timbre]
            [zengarden.util :as zu]))

(zu/turn-on-validation)


(defn context-input-predicate
  "Test that context input is only :keywords or
   vectors of :keywords"
  [input]

  (every? #(or (keyword? %)
               (and
                (vector? %)
                (every? keyword? %)))
          input))

(s/defn process-element :- s/Str
  [element :- s/Keyword
   context :- (s/pred context-input-predicate) ]

  (if-not (empty? context)
    (reduce #(str %1 " " %2)
            (map name
                 (conj (into [] context) element)))
    (name element)))


(s/defn process-attributes :- s/Str

  ([attributes :- {s/Keyword s/Str}]
     (process-attributes attributes true))

  ([attributes :- {s/Keyword s/Str}
    pretty :- s/Bool]

     (let [attribute-strings (conj (reduce (fn [rlt e]
                                             (conj rlt (str
                                                        (if pretty (with-out-str (newline)) "")
                                                        (if pretty "  " "")
                                                        (first e) " : " (second e) ";")))
                                           ["{"]
                                           (map (fn [x] [(name (first x)) (second x)])
                                                (seq attributes)))
                                   "}")

           attributeS (reduce #(str %1 " " %2) attribute-strings)]

       attributeS)))
