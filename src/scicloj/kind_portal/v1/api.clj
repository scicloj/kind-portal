(ns scicloj.kind-portal.v1.api
  (:require [portal.api :as portal]
            [scicloj.kindly.v3.api :as kindly]
            [scicloj.kind-portal.v1.impl :as impl]
            [scicloj.kind-portal.v1.session :as session]))

(defn open-if-needed []
  (session/open-if-needed))

(defn kindly-submit-context [context]
  (open-if-needed)
  (-> context
      impl/prepare
      (->> (vector :portal.viewer/inspector))
      impl/as-portal-hiccup
      portal/submit)
  :ok)
