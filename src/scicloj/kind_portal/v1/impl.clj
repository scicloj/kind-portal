(ns scicloj.kind-portal.v1.impl
  (:require [portal.api :as portal]
            [scicloj.kindly.v3.api :as kindly]
            [scicloj.kind-portal.v1.walk :as careful-walk]
            [scicloj.kind-portal.v1.util.image :as util.image]))

(def *kind->viewer
  (atom {}))

(defn add-viewer!
  [kind viewer]
  (kindly/add-kind! kind)
  (swap! *kind->viewer assoc kind viewer))

(defn maybe-apply-viewer [value kind]
  (if-let [viewer (@*kind->viewer kind)]
    (viewer value)
    value))

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
           (-> v first namespace (= "porta.viewer")))
    (as-portal-hiccup v)
    v))

(defn prepare [{:keys [form value]
                :or {value (eval form)}}]
  (let [{:keys [value kind]} (-> {:form form
                                  :value value}
                                 kindly/advice
                                 first)]
    (if kind
      (maybe-apply-viewer value kind)
      (->> value
           (careful-walk/postwalk
            (fn [subvalue]
              (let [{:keys [value kind]}
                    (-> {:value subvalue}
                        kindly/advice
                        first)]
                (if kind
                  (-> subvalue
                      (maybe-apply-viewer kind))
                  subvalue))))))))


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
 :kind/code
 (fn [v]
   (->> v
        (map (fn [code]
               [:portal.viewer/code code]))
        (into [:div])
        as-portal-hiccup)))

(add-viewer!
 :kind/md
 (fn [v]
   (->> v
        (map (fn [md]
               [:portal.viewer/markdown md]))
        (into [:div])
        as-portal-hiccup)))

(add-viewer!
 :kind/table-md
 (fn [v]
   (->> [:code (render-md v)]
        as-portal-hiccup)))

(add-viewer!
 :kind/dataset
 (fn [v]
   (-> [:code (-> v
                  println
                  with-out-str
                  vector
                  (kindly/consider :kind/table-md)
                  render-md)]
       as-portal-hiccup)))

(add-viewer!
 :kind/buffered-image
 util.image/buffered-image->byte-array)
