(ns tesseract.attrs-test
  #+clj (:require [clojure.test :refer :all]
                  [tesseract.attrs :as attrs]
                  [tesseract.dom :as dom]
                  [tesseract.cursor])
  #+cljs (:require-macros [cemerick.cljs.test
                           :refer (is deftest with-test run-tests testing test-var)])
  #+cljs (:require [cemerick.cljs.test :as t]
                   [tesseract.attrs :as attrs :include-macros true]
                   [tesseract.cursor]
                   [tesseract.dom :as dom]))

(deftest test-attrs-diff
  (testing "no difference"
    (let [a {:class :some-class :id :some-id}]
      (is (nil? (seq (attrs/attrs-diff a a))))))
  (let [cases [[{:a 1}
                {:a 1 :b 1}
                [[:set-attr :b 1]]]
               [{:a 1 :b 1}
                {:a 1}
                [[:remove-attr :b]]]
               [{:a 1}
                {:b 1}
                [[:set-attr :b 1] [:remove-attr :a]]]
               [{:style {:height 1}}
                {:style {:height 2}}
                [[:set-style :height 2]]]
               [{:style {:height 1}}
                {:style {:height 1 :width 2}}
                [[:set-style :width 2]]]
               [{:style {:height 1 :width 1}}
                {:style {:height 2 :width 2}}
                [[:set-style :width 2] [:set-style :height 2]]]
               [{:class [:a :b]}
                {:class [:a]}
                [[:set-attr :class [:a]]]]]]
    (doseq [[prev next expected] cases]
      (is (= (set expected) (set (attrs/attrs-diff prev next)))))))

(deftest test-build-attrs
  (is (= {:class "foo bar"}
         (-> (dom/div {:on-click (fn [e] nil)
                       :class [:foo :bar]})
             (attrs/build-attrs nil)
             (attrs/get-attrs)))))

(deftest test-listener-registration
  (let [cursor [:root-id 0 0]
        env (atom {})
        event-name :click
        listener (fn [e c])
        ks [:listeners event-name cursor]]
    (attrs/register-listener! env event-name cursor listener)
    (is (= listener (attrs/get-listener env event-name cursor)))
    (attrs/unregister-listener! env event-name cursor)
    (is (nil? (attrs/get-listener env event-name cursor)))))

(deftest test-attr-env
  (binding [attrs/*attr-env* (atom {})]
    (let [cursor [:root-id 1]
          component (-> (dom/div {}) (tesseract.cursor/assoc-cursor cursor))
          listener (fn [e c])]
      (attrs/build-attr {} component :on-click listener nil)
      (is (= listener (attrs/get-listener attrs/*attr-env* :click cursor)))
      (attrs/build-attr {} component :on-click nil listener)
      (is (nil? (attrs/get-listener attrs/*attr-env* :click cursor))))))

(deftest test-with-attr-env
  (let [env (atom {})
        prev-env attrs/*attr-env*]
    (attrs/with-attr-env env
      (is (= env attrs/*attr-env*)))
    (is (= prev-env attrs/*attr-env*))))
