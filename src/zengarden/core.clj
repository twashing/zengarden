(ns zengarden.core
  (:require [schema.core :as s]
            [taoensso.timbre :as timbre]

            [zengarden.process :as zp]
            [zengarden.walk :as zw]
            [zengarden.util :as zu]))

(zu/turn-on-validation)
(timbre/set-level! :warn)

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
  ([input :- (s/pred css-input-predicate)]
   (css input true))
  ([input :- (s/pred css-input-predicate)
    pretty :- s/Bool]

     (zw/walka input [] pretty "")))
