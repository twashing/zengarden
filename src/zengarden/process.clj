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
