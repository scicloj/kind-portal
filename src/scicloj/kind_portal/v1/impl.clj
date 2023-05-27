(ns scicloj.kind-portal.v1.impl
  (:require [portal.api :as portal]
            [scicloj.kindly.v3.api :as kindly]
            [scicloj.kind-portal.v1.walk :as careful-walk]))

(def *kind->viewer
  (atom {}))

(defn add-viewer!
  [kind viewer]
  (swap! *kind->viewer assoc kind viewer))

(defn maybe-apply-viewer [value kind]
  (if-let [viewer (@*kind->viewer kind)]
    (viewer value)
    value))

(defn as-portal-hiccup [v]
  (-> v
      (vary-meta assoc
                 :portal.viewer/default
                 :portal.viewer/hiccup)))

(defn as-portal-hiccup-if-relevant [v]
  (if (and (vector? v)
           (-> v first keyword)
           (-> v first namespace (= "porta.viewer")))
    (as-portal-hiccup v)
    v))

(defn prepare [form]
  (let [{:keys [value kind]} (-> {:form form
                                  :value (eval form)}
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

;; (add-viewer!
;;  :kind/dataset
;;  (fn [v]
;;    (-> v
;;        println
;;        with-out-str
;;        vector
;;        (kindly/consider :kind/table-md))))
