(ns scicloj.kind-portal.v1.impl
  (:require [portal.api :as portal]
            [scicloj.kindly.v3.api :as kindly]
            [scicloj.kind-portal.v1.walk :as careful-walk]
            [scicloj.kind-portal.v1.util.image :as util.image]
            [clojure.pprint :as pp]))

(def *kind->viewer
  (atom {}))

(defn add-viewer!
  [kind viewer]
  (kindly/add-kind! kind)
  (swap! *kind->viewer assoc kind viewer))

(defn value->kind [v]
  (-> {:value v}
      kindly/advice
      ;; TODO: handle multiple contexts more wisely
      first
      :kind))

(defn as-portal [v portal-viewer-name]
  (-> v
      (vary-meta assoc
                 :portal.viewer/default
                 portal-viewer-name)))

(defn as-portal-hiccup [v]
  (-> v
      (as-portal :portal.viewer/hiccup)))

(defn as-portal-hiccup-if-relevant [v]
  (if (and (vector? v)
           (-> v first keyword)
           (-> v first namespace (= "portal.viewer")))
    (as-portal-hiccup v)
    v))

(defn pprint-viewer
  ([]
   (pprint-viewer nil))
  ([prefix]
   (fn [v]
     (let [printed-value [:portal.viewer/code
                          (-> v
                              pp/pprint
                              with-out-str)]]
       (as-portal-hiccup
        (if prefix
          [:div prefix printed-value]
          printed-value))))))

(defn fallback-viewer [kind]
  (pprint-viewer [:p "unimplemented kind" [:code (pr-str kind)]]))

(defn kind-viewer [kind]
  (or (@*kind->viewer kind)
      (fallback-viewer kind)))

(defn prepare [context]
  (let [{:keys [value kind]} (-> context
                                 kindly/advice
                                 ;; TODO: handle multiple inferred contexts more wisely
                                 first)]
    ((kind-viewer kind) value)))




(defn complete-context [{:keys [form]
                         :as context}]
  (if (contains? context :value)
    context
    (assoc context :value (eval form))))

(defn prepare-value [v]
  (prepare {:value v}))

(add-viewer!
 :kind/pprint
 pprint-viewer)

(add-viewer!
 :kind/void
 (constantly
  (as-portal-hiccup
   [:p ""])))

(add-viewer!
 :kind/vega-lite
 (fn [v]
   (as-portal-hiccup
    [:portal.viewer/vega-lite v])))

(add-viewer!
 :kind/hiccup
 (fn [v] (as-portal-hiccup v)))

(defn render-md [v]
  (->> v
       ((fn [v]
          (if (vector? v) v [v])))
       (map (fn [md]
              [:portal.viewer/markdown md]))
       (into [:div])
       as-portal-hiccup))

(add-viewer!
 :kind/md
 render-md)

(add-viewer!
 :kind/code
 (fn [v]
   (->> v
        (map (fn [code]
               [:portal.viewer/code code]))
        (into [:div])
        as-portal-hiccup)))

(add-viewer!
 :kind/dataset
 (fn [v]
   (-> [:code (-> v
                  println
                  with-out-str
                  render-md)]
       as-portal-hiccup)))

(add-viewer!
 :kind/buffered-image
 util.image/buffered-image->byte-array)

(add-viewer!
 :kind/vector
 (partial mapv prepare-value))

(add-viewer!
 :kind/seq
 (partial map prepare-value))

(add-viewer!
 :kind/set
 (comp set
       (partial map prepare-value)))

(add-viewer!
 :kind/map
 (fn [m]
   (-> m
       (update-keys prepare-value)
       (update-vals prepare-value))))
