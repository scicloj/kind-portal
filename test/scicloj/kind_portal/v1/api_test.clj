(ns scicloj.kind-portal.v1.api_test
  (:require  [scicloj.kind-portal.v1.api :as kind-portal]
             [scicloj.kindly.v4.kind :as kind]
             [scicloj.kindly-advice.v1.api :as kindly-advice]
             [tablecloth.api :as tc]))

(kind-portal/kindly-submit-context
 {:form '(kind/hiccup
          [:h1 "a"])})
