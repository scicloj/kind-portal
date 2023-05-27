(ns scicloj.kind-portal.v1.api
  (:require [portal.api :as portal]
            [scicloj.kindly.v3.api :as kindly]
            [scicloj.kind-portal.v1.impl :as impl]
            [scicloj.kind-portal.v1.session :as session]))

(defn open-if-needed []
  (session/open-if-needed))

(defn kindly-submit-form [form]
  (open-if-needed)
  (-> form
      impl/prepare
      (->> (vector :portal.viewer/inspector))
      impl/as-portal-hiccup
      portal/submit))


(comment
  (require '[scicloj.kindly-default.v1.api :as kindly-default])

  (kindly-default/setup!)

  (open-if-needed)

  (kindly-submit-form '(+ 1 2))

  (kindly-submit-form '(-> [:h1 "a"]
                           (kindly/consider :kind/hiccup)))

  (kindly-submit-form '(->> (range 3)
                            (map (fn [i]
                                   (-> [:h1 i]
                                       (kindly/consider :kind/hiccup))))))

  (kindly-submit-form '(-> {:$schema "https://vega.github.io/schema/vega-lite/v5.json"
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
                            :mark "bar"}
                           (kindly/consider :kind/vega-lite)))


  (-> [:h1 "a"]
      (kindly/consider :kind/hiccup))

  ,)
