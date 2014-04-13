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

(s/defn process-element-brackets :- s/Str
  [element :- [s/Any]
   context :- (s/pred context-input-predicate)]

  (timbre/debug "process-element-bracket / element[" element "] / context[" context "]")

  (str (reduce (fn [rlt e]
                 (if (number? e)
                   e
                   (name e)))
                "("
                element)
       ")"))

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

(defn transform-query-term [qterm]
  {:pre [(or (= 1 (count qterm))
             (= 2 (count qterm)))]}

  (if (= 2 (count qterm))
    (str "(" (name (first qterm)) ": " (name (second qterm)) ")")
    (str "(" (name (first qterm)) ")")))

(defn process-media-query [query]

  (reduce (fn [rlt ec]
            (let [qterm (if (string? ec)
                          ec
                          (transform-query-term ec))]
              (conj rlt qterm)))
          []
          query))

(defn process-media-head [node]

  (let [media-query (->> node (filter map?) first :media-queries)
        processed-query (process-media-query media-query)

        rules (->> node (filter vector?) first)]

    (reduce (fn [rlt e] (str rlt " " e))
            "@media"
            processed-query)))

(defn url-or-uri-predicate [node]

  (let [attrs (->> node (filter map?) first)]

    (or (and (:url attrs)
             (not (:uri attrs)))
        (and (not (:url attrs))
             (:uri attrs)))))

(s/defn process-import :- s/Str

  [node :- (s/pred url-or-uri-predicate)]

  (let [nattrs (->> node (filter map?) first)

        url (:url nattrs)
        uri (:uri nattrs)
        media-query (:media-queries nattrs)]

    (str "@import "
         (if url
           (str "url(\"" url "\")")
           (str "\"" uri "\""))
         (if media-query
           (str " "
                (reduce (fn [rlt e] (str rlt " " e))
                        (process-media-query media-query))))
         ";")))
