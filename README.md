# per-ring-request

Tiny library for memoizing code such that it runs once per Ring request.

## Installation

Leiningen:

    :dependencies [[per-ring-request "0.1.0"]]

## Usage

All the usages below require the middleware to be set up. For example, with Noir, do

```clojure
(use 'per-ring-request.middleware)

(server/add-middleware wrap-setup-memoization)
```

Memoization only occurs within the dynamic scope of this middleware.

### Memoize a block of code

```clojure
(use 'per-ring-request.core)

(per-request
  (do-something-expensive))
```

### Memoize a closure

This is equivalent to the previous example:

```clojure
(use 'per-ring-request.core)

(def my-function
  (memoize-per-request
    (fn [foo]
      (do-something-expensive))))
```

### Note on concurrency

If within a request you use a concurrency mechanism, such as Clojure's `future` macro, that reuses current bindings in another thread, memoization will be shared with that thread. In this case there is a race condition - the memoized code might be evaluated more than once.

### Difference between `per-request` and `memoize-per-request`

```clojure
(use 'per-ring-request.core)

(defn make-closure-1 []
  (fn []
    (per-request
      (println "hi"))))
(def a (make-closure-1))
(def b (make-closure-1))
; Even if you call a and b in the same request, "hi" will only be printed once.

(defn make-closure-2 []
  (memoize-per-request
    (fn []
      (println "hi"))))
(def c (make-closure-1))
(def d (make-closure-2))
; Calling c and d in the same request will print "hi" twice.
```

## License

Copyright © 2012 Jacob Williams

Distributed under the Eclipse Public License, the same as Clojure.
