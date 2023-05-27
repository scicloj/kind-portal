(defun kind-portal/open-if-needed ()
  (interactive)
  (cider-interactive-eval "
    (require '[scicloj.kind-portal.v1.api])
    (scicloj.kind-portal.v1.api/open-if-needed)")
  t)

(defun kind-portal/cider-interactive-notify-and-eval (code)
  (cider-interactive-eval
   code
   (cider-interactive-eval-handler nil (point))
   nil
   nil))

(defun kind-portal/send (code)
  (clay/start)
  (kind-portal/cider-interactive-notify-and-eval
   (concat "
     (require '[scicloj.kind-portal.v1.api])
     (scicloj.kind-portal.v1.api/kindly-submit-form (quote " code "))")))

(defun kind-portal/send-last-sexp ()
  (interactive)
  (kind-portal/send (cider-last-sexp)))

(defun kind-portal/send-defun-at-point ()
  (interactive)
  (kind-portal/send (thing-at-point 'defun)))
