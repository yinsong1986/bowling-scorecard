(ns bowling-scorecard.core-test
  (:require [clojure.test :refer :all]
            [bowling-scorecard.core :refer :all]))

(deftest new-scorecard-test
  (testing "creating a new empty scorecard"
    (is (= [] (new-scorecard)))))

(deftest score-a-frame-test
  (testing "Score the first frame."
    (is (= [["7" "/"]] (score-a-frame [] "7" "/"))))
  (testing "Score the correct last frame."
    (is (= [["x"] ["x"] ["x"] ["x"] ["x"] ["x"] ["x"] ["x"] ["x"] ["x" "7" "/"]] (score-a-frame [["x"] ["x"] ["x"] ["x"] ["x"] ["x"] ["x"] ["x"] ["x"]] "x" "7" "/"))))
  (testing "Return nil for the wrong last frame."
    (is (= nil (score-a-frame [["x"] ["x"] ["x"] ["x"] ["x"] ["x"] ["x"] ["x"] ["x"]] "3" "7" "/")))))
