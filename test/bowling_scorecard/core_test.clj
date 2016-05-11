(ns bowling-scorecard.core-test
  (:require [clojure.test :refer :all]
            [bowling-scorecard.core :refer :all]))

(deftest new-scorecard-test
  (testing "creating a new empty scorecard"
    (is (= [] (new-scorecard)))))
