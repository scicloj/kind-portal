;; This namespace demonstrates how one may generate a standalone HTML file
;; containing Portal views annotated by Kindly and prepared using kind-portal.
;; This is a self-contained demonstration of the method internally used by
;; [Clay](https://scicloj.github.io/clay/)
;; to [embed Portal views](https://scicloj.github.io/clay/#embedded-portal) in notebooks.
;; See the resulting HTML page [here](https://scicloj.github.io/kind-portal/demo).
;;
;; To generate the page, call the `generate!` function.

(ns standalone-html
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.kind-portal.v1.api :as kind-portal]
            [portal.api :as portal]
            [hiccup.core]
            [hiccup.page]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(defn pr-str-with-meta [value]
  (binding [*print-meta* true]
    (pr-str value)))

(defonce portal-dev
  (portal/url
   (portal/start {})))

(def portal-js-url (let [[host query] (str/split portal-dev #"\?")]
                     (str host "/main.js?" query)))

(defn portal [value]
  [:div
   [:script
    (->> {:value value}
         kind-portal/prepare
         pr-str-with-meta
         pr-str
         (format "portal_api.embed().renderOutputItem(
                {'mime': 'x-application/edn',
                 'text': (() => %s)}
                , document.currentScript.parentElement);"))]])


(defn fetch-portal-js! [target-path]
  (io/make-parents target-path)
  (->> portal-js-url
       slurp
       (spit target-path)))

(defn page [{:keys [title
                    portal-js-path
                    values]}]
  (hiccup.page/html5
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:link {:rel "icon" :href "data:,"}] ; avoid favicon.ico request: https://stackoverflow.com/a/38917888
    [:title title]]
   [:body
    (hiccup.page/include-js portal-js-path)
    (->> values
         (map portal)
         (into [:div]))]))

(defn generate-demo! []
  (fetch-portal-js! "docs/demo_files/portal.js")
  (let [title "Kind-portal standalone HTML demo"]
    (->> {:title title
          :portal-js-path "demo_files/portal.js"
          :values [(kind/hiccup [:div [:h1 title]])
                   (kind/md "This is a static html page with [Portal](https://github.com/djblue/portal) views of values annotated by [Kindly](https://scicloj.github.io/kindly/) and prepared using the [kind-portal](https://github.com/scicloj/kind-portal) adapter.")
                   (kind/hiccup [:a {:href "https://github.com/scicloj/kind-portal/blob/main/examples/standalone_html.clj"}
                                 "(source script)"])
                   (kind/md "A plot:")
                   (kind/vega-lite {:$schema "https://vega.github.io/schema/vega-lite/v5.json"
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
                                    :mark "bar"})
                   (kind/md "A vector with a few nested Hiccup values:")
                   [(kind/hiccup [:p "text"])
                    (kind/hiccup [:big "big text"])
                    (kind/hiccup [:big [:big "big big text"]])]]}
         page
         (spit "docs/demo.html"))))

(comment
  (generate-demo!))
