(ns scicloj.kind-portal.v1.session
  (:require [portal.api :as portal]))

(def *portal-session (atom nil))

(defn open-if-needed []
  (if-let [p @*portal-session]
    (portal/open p)
    (reset! *portal-session (portal/open))))
