(ns per-ring-request.core-test
  (:use clojure.test
        per-ring-request.core
        per-ring-request.middleware))

(deftest t-per-request-unwrapped
  (let [calls (atom 0)
        func (fn [] (per-request
                      (swap! calls inc)
                      'hey))]
    (is (= 'hey (func)))
    (is (= 'hey (func)))
    (is (= 2 @calls))))

(deftest t-per-request
  (let [calls1 (atom 0)
        calls2 (atom 0)
        func1 (fn [] (per-request
                       (swap! calls1 inc)
                       'hey))
        func2 (fn [] (per-request
                       (swap! calls2 inc)
                       'hi))]
    ((wrap-setup-memoization
      (fn [req]
        (is (= 'blah req))
        (is (= 'hey (func1)))
        (is (= 'hi (func2)))
        (is (= 'hey (func1)))
        (is (= 'hi (func2))))) 'blah)
    (is (= 1 @calls1))
    (is (= 1 @calls2))))

(deftest t-memoize-per-request-unwrapped
  (let [calls (atom 0)
        func (memoize-per-request
               (fn [a b]
                 (swap! calls inc)
                 (+ a b)))]
    (is (= 3 (func 1 2)))
    (is (= 3 (func 1 2)))
    (is (= 2 @calls))))

(deftest t-memoize-per-request
  (let [calls1 (atom 0)
        calls2 (atom 0)
        func1 (memoize-per-request
                (fn [a b]
                  (swap! calls1 inc)
                  (+ a b)))
        func2 (memoize-per-request
                (fn [a b]
                  (swap! calls2 inc)
                  (- a b)))]
    ((wrap-setup-memoization
       (fn [req]
         (is (= 'blah req))
         (is (= 3 (func1 1 2)))
         (is (= -1 (func2 1 2)))
         (is (= 3 (func1 1 2)))
         (is (= -1 (func2 1 2)))
         (is (= 4 (func1 1 3))))) 'blah)
    (is (= 2 @calls1))
    (is (= 1 @calls2))))
