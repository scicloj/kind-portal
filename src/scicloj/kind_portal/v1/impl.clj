(ns scicloj.kind-portal.v1.impl
  (:require [scicloj.kindly-advice.v1.api :as kindly-advice]
            [scicloj.kind-portal.v1.util.image :as util.image]
            [clojure.pprint :as pp]))

(def *kind->preparer
  (atom {}))

(defn add-preparer!
  [kind preparer]
  (swap! *kind->preparer assoc kind preparer))

(defn value->kind [v]
  (-> {:value v}
      kindly-advice/advise
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

(defn fallback-preparer [kind]
  (fn [v]
    (as-portal-hiccup
     [:div
      (when kind
        [:p "unimplemented kind" [:code (pr-str kind)]])
      [:portal.viewer/inspector v]])))

(defn kind-preparer [kind]
  (or (@*kind->preparer kind)
      (fallback-preparer kind)))

(defn prepare [{:as context
                :keys [value]}]
  ((-> context
       kindly-advice/advise
       :kind
       kind-preparer)
   value))

(defn complete-context [{:keys [form]
                         :as context}]
  (if (contains? context :value)
    context
    (assoc context :value (eval form))))

(defn prepare-value [v]
  (prepare {:value v}))

(defn pprint-preparer
  ([]
   (pprint-preparer nil))
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

(add-preparer!
 :kind/pprint
 (pprint-preparer))

(add-preparer!
 :kind/void
 (constantly
  (as-portal-hiccup
   [:p ""])))

(add-preparer!
 :kind/vega-lite
 (fn [v]
   (as-portal-hiccup
    [:portal.viewer/vega-lite v])))

(add-preparer!
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

(add-preparer!
 :kind/md
 render-md)

(add-preparer!
 :kind/code
 (fn [v]
   (->> v
        (map (fn [code]
               [:portal.viewer/code code]))
        (into [:div])
        as-portal-hiccup)))

(add-preparer!
 :kind/dataset
 (fn [v]
   (-> [:code (-> v
                  println
                  with-out-str
                  render-md)]
       as-portal-hiccup)))

(add-preparer!
 :kind/image
 util.image/buffered-image->byte-array)

(add-preparer!
 :kind/vector
 (partial mapv prepare-value))

(add-preparer!
 :kind/seq
 (partial map prepare-value))

(add-preparer!
 :kind/set
 (comp set
       (partial map prepare-value)))

(add-preparer!
 :kind/map
 (fn [m]
   (into (empty m)
         (for [[k v] m]
           [(prepare-value k) (prepare-value v)]))))
