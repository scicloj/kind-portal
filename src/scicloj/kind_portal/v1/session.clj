(ns scicloj.kind-portal.v1.session
  (:require [clojure.java.io :as io]
            [portal.api :as portal]))

(def *portal-session (atom nil))

(defn detect-editor
  "When you run the portal plugin in an IDE, it creates a config file in `.portal/editor.edn`
  See https://cljdoc.org/d/djblue/portal/0.46.0/doc/editors for more information.
  If we detect the file you don't need to set the configuration manually."
  []
  (or (and (.exists (io/file ".portal" "emacs.edn"))
           {:launcher :emacs})
      (and (.exists (io/file ".portal" "vs-code.edn"))
           {:launcher :vs-code})
      (and (.exists (io/file ".portal" "intellij.edn"))
           {:launcher :intellij})
      {}))

(defn open-if-needed
  "Like portal/open, but stores the result in *portal-session.
  If there is already a session, it makes sure that it is open,
  Otherwise opens a new inspector window."
  ([] (open-if-needed (detect-editor)))
  ([config] (swap! *portal-session portal/open config)))
