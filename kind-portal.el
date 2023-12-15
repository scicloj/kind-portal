(defun kind-portal/open-if-needed ()
  (interactive)
  (cider-interactive-eval "
    (require '[scicloj.kind-portal.v1.api])
    (scicloj.kind-portal.v1.api/open-if-needed)")
  t)

(defun kind-portal/send (code)
  (cider-interactive-eval
   (concat "
     (require '[portal.api]
              '[scicloj.kind-portal.v1.api])
     (portal.api/clear)
     (scicloj.kind-portal.v1.api/kindly-submit-context {:form (quote " code ")})")))

(defun kind-portal/send-last-sexp ()
  (interactive)
  (kind-portal/send (cider-last-sexp)))

(defun kind-portal/send-defun-at-point ()
  (interactive)
  (kind-portal/send (thing-at-point 'defun)))
