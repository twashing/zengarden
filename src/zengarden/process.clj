(ns zengarden.process
  (:require [schema.core :as s]
            [zengarden.util :as zu]))

(zu/turn-on-validation)


(s/defn process-element :- s/Str
  [element :- s/Keyword
   context :- [s/Keyword]]

  (if-not (empty? context)
    (reduce #(str %1 " " %2)
            (map name
                 (conj context element)))
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
