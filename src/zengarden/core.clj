(ns zengarden.core)


(defn combinator-identity [thing]
  (fn [] (name thing)))

(defn combinator-attributes [attrs contextfn]

  (let [r3 (seq attrs)
        r4 (map (fn [x]
                  [(combinator-identity (first x))
                   (combinator-identity (second x))])
                r3)
        r5 (reduce
            (fn [rt e]
              (conj rt
                    [((first e))
                     ((second e))]))
            []
            r4)]

    (if-not (nil? contextfn)
      [(contextfn) r5]
      r5))
  )

(defn combinator-element [elem]
  (fn [x]
    (if (nil? x)
      ((combinator-identity elem))
      :asdf)))

(defn one [elem-list]

  (->> elem-list
       (map combinator-element)
       ;;(reduce str)
       ))

(declare identite element attributes)

(defn identite [thing]
  (name thing))

(defn element
  ([elem]
     (identite elem))
  ([elem attrs]
     [(identite elem) (attributes attrs)]))

(defn attributes [attrs]
  (let [r3 (seq attrs)
        r4 (map (fn [x]
                  [(combinator-identity (first x))
                   (combinator-identity (second x))])
                r3)
        r5 (reduce
            (fn [rt e]
              (conj rt
                    [((first e))
                     ((second e))]))
            []
            r4)]
    r5))

(comment

  (def v [:html {:height "100%" :display "flex"}
          [:body {:display "flex"}
           [:.myclass {:float "left"}]]])
  (def w [:html {:height "100%" :display "flex"}
          [:body {:display "flex"}]])

  (def x [:html {:height "100%" :display "flex"}])

  (def y {:height "100%" :display "flex"})
  (def z [:html :body])

  ;; Elements
  (def r1 (map combinator-identity z))
  (def r2 (reduce
           (fn [rt e]
             (conj rt (e)))
           []
           r1))

  ;; Attributes
  (def r3 (seq y))
  (def r4 (map (fn [x]
                 [(combinator-identity (first x))
                  (combinator-identity (second x))])
               r3))
  (def r5 (reduce
           (fn [rt e]
             (conj rt
                   [((first e))
                    ((second e))]))
           []
           r4))

  )
