(ns scicloj.kind-portal.v1.walk)

;; a dataset-friendly version of clojure.walk
;; copied from the Clay code base
;; needs to be extracted as a separate library

(defn walk
  [inner outer form]
  (-> (cond
        (-> form outer class str
            (#{"class tech.v3.dataset.impl.column.Column"
               "class tech.v3.dataset.impl.dataset.Dataset"}))
        form
        ;;
        (list? form)
        (outer (apply list (map inner form)))
        ;;
        (instance? clojure.lang.IMapEntry form)
        (outer (clojure.lang.MapEntry/create (inner (key form)) (inner (val form))))
        ;;
        (seq? form) (outer (doall (map inner form)))
        ;;
        (instance? clojure.lang.IRecord form)
        (outer (reduce (fn [r x] (conj r (inner x))) form form))
        ;;
        (coll? form)
        (outer (into (empty form) (map inner form)))
        ;;
        :else (outer form))))

(defn postwalk
  [f form]
  (walk (partial postwalk f) f form))

(defn prewalk
  [f form]
  (walk (partial prewalk f) identity (f form)))
