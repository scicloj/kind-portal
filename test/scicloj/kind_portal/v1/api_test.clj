(ns scicloj.kind-portal.v1.api_test
  (:require [clojure.test :refer [deftest testing is]]
            [scicloj.kind-portal.v1.api :as kind-portal]
            [scicloj.kindly.v4.kind :as kind]))

(deftest submit-context-test
  (kind-portal/kindly-submit-context
    {:form `(kind/hiccup [:h1 "a"])}))

(deftest preparation-test
  (is (= "^#:kindly{:kind :kind/map} {^{:kindly/kind :kind/hiccup, :portal.viewer/default :portal.viewer/hiccup} [:div \"A\"] ^{:kindly/kind :kind/hiccup, :portal.viewer/default :portal.viewer/hiccup} [:div \"B\"], ^#:portal.viewer{:default :portal.viewer/hiccup} [:div nil [:portal.viewer/inspector :c]] {^#:portal.viewer{:default :portal.viewer/hiccup} [:div nil [:portal.viewer/inspector :d]] ^{:kindly/kind :kind/hiccup, :portal.viewer/default :portal.viewer/hiccup} [:div \"E\"]}}"
         (binding [*print-meta* true]
           (pr-str (kind-portal/prepare {:value (kind/map {(kind/hiccup [:div "A"]) (kind/hiccup [:div "B"])
                                                           :c                       {:d (kind/hiccup [:div "E"])}})}))))))
