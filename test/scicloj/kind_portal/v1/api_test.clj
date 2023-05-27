(ns scicloj.kind-portal.v1.api_test
  (:require  [scicloj.kind-portal.v1.api :as kind-portal]
             [scicloj.kindly-default.v1.api :as kindly-default]
             [scicloj.kindly.v3.kind :as kind]))

(kindly-default/setup!)


(kind-portal/kindly-submit-form '(+ 1 2))

(kind-portal/kindly-submit-form '(kind/hiccup
                                  [:h1 "a"]))

(kind-portal/kindly-submit-form '(kind/vega-lite
                                  {:$schema "https://vega.github.io/schema/vega-lite/v5.json"
                                   :data {:values [{:a "A" :b 28}
                                                   {:a "B" :b 55}
                                                   {:a "C" :b 43}
                                                   {:a "D" :b 91}
                                                   {:a "E" :b 81}
                                                   {:a "F" :b 53}
                                                   {:a "G" :b 19}
                                                   {:a "H" :b 87}
                                                   {:a "I" :b 52}]}
                                   :description "A simple bar chart with embedded data."
                                   :encoding {:x {:axis {:labelAngle 0} :field "a" :type "nominal"}
                                              :y {:field "b" :type "quantitative"}}
                                   :mark "bar"}))


(kind/hiccup [:h1 "a"])


,
