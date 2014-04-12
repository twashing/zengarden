(ns zengarden.process
  (:require [clojure.string :as str]
            [schema.core :as s]
            [taoensso.timbre :as timbre]
            [zengarden.util :as zu]))

(zu/turn-on-validation true)


(defn context-input-predicate
  "Test that context input is a vector, with only :keywords or
   vectors of :keywords"
  [input]

  (and (vector? input)
       (every? #(or (keyword? %)
                    (and
                     (vector? %)
                     (every? keyword? %)))
               input)))

(defn join-nested-contexts
  "transform something like [\"a b\" \"x y\"] and [:c :d]
   into [\"a b c\" \"a b d\" \"x y c\" \"x y d\"]"
  [inp]

  (reduce (fn [rlt e]

            (let [ii (if (keyword? e) [e] e)]

              (for [i rlt j ii]
                (str i " " (name j)))))

          [""]
          inp))

(s/defn process-element :- s/Str
  [element :- s/Keyword
   context :- (s/pred context-input-predicate) ]

  (timbre/debug "process-element / element[" element "] / context[" context "]")
  (if-not (empty? context)

    (if (every? keyword? context)

      (reduce #(str %1 " " %2)
              (map name
                   (conj (into [] context) element)))

      (str/join ","
                (map #(str % " " (name element))
                     (join-nested-contexts context))))

    (name element)))


(s/defn process-attributes :- s/Str

  ([attributes :- {s/Keyword s/Str}]
     (process-attributes attributes true))

  ([attributes :- {s/Keyword s/Str}
    pretty :- s/Bool]

     (let [attribute-strings (conj (reduce (fn [rlt e]
                                             (conj rlt (str
                                                        (if pretty (with-out-str (newline)))
                                                        (if pretty "  ")
                                                        (first e) " : " (second e) ";")))
                                           ["{"]
                                           (map (fn [x] [(name (first x)) (second x)])
                                                (seq attributes)))
                                   "}")

           attributeS (reduce #(str %1 " " %2) attribute-strings)]

       attributeS)))

(s/defn process-namespace :- s/Str

  ([node]
     (process-namespace node false))
  ([node pretty]

     (let [prefix (:prefix (first (filter map? node)))
           url (:url (first (filter map? node)))]

       (str "@namespace "
            (if prefix (str prefix " "))
            "url(" url ");"))))
