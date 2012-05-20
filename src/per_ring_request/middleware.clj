(ns per-ring-request.middleware
  (:import [java.util Collections HashMap])
  (:use per-ring-request.core))

(defn wrap-setup-memoization
  "Per-request memoization will only take effect if this
   middleware is applied."
  [handler]
  (fn [req]
    (binding [*memoizations* (Collections/synchronizedMap (HashMap.))]
      (handler req))))
