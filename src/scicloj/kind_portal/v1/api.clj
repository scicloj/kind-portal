(ns scicloj.kind-portal.v1.api
  (:require [portal.api :as portal]
            [scicloj.kind-portal.v1.prepare :as prepare]
            [scicloj.kind-portal.v1.session :as session]))

(defn open-if-needed []
  (session/open-if-needed))

(defn prepare [context]
  (prepare/prepare context))

(defn kindly-submit-context [context]
  (open-if-needed)
  (-> context
      prepare/complete-context
      prepare
      (->> (vector :portal.viewer/inspector))
      prepare/as-portal-hiccup
      portal/submit)
  :ok)
