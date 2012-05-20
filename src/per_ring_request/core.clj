(ns per-ring-request.core)

(def ^:dynamic *memoizations* nil)

(defmacro per-request-by
  [id & body]
  `(if-let [result# (get *memoizations* ~id)]
     result#
     (let [result# (do ~@body)]
       (when *memoizations*
         (.put *memoizations* ~id result#))
       result#)))

(defmacro per-request
  "Creates a code block which saves its result after the
   first invocation during each request."
  [& body]
  (let [id (gensym)]
    `(per-request-by '~id ~@body)))

(defn memoize-per-request
  "Wraps the given function so that, for a given argument list,
   it saves the result after the first invocation during each request."
  [func]
  (fn [& args]
    (per-request-by
      (cons (System/identityHashCode func) args)
      (apply func args))))
