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

(deftest intize-balls-test
  (testing "Transform a 3-ball frame to integers."
    (is (= [10 7 3] (intize-balls ["x" "7" "/"])))
    (is (= [10 7 2] (intize-balls ["x" "7" "2"]))))
  (testing "Transform a 2-ball frame to integers."
    (is (= [7 3] (intize-balls ["7" "/"])))
    (is (= [7 2] (intize-balls ["7" "2"]))))
  (testing "Transform a 1-ball frame to integers."
    (is (= [10] (intize-balls ["x"])))))

(deftest strike?-test
  (testing "check if is a strike"
    (is (= true (strike? [10 3 4])))
    (is (= false (strike? [5 3 4])))))

(deftest spare?-test
  (testing "check if is a spare"
    (is (= true (spare? [7 3 4])))
    (is (= false (spare? [5 3 4])))))

(deftest balls-a-frame-score-test
  (testing "Count the balls contributing to a frame's score."
    (is (= 3 (balls-a-frame-score [10 3 4 5])))
    (is (= 3 (balls-a-frame-score [7 3 4 5])))
    (is (= 2 (balls-a-frame-score [5 3 4 5])))))

(deftest balls-a-frame-test
  (testing "Count the balls of a frame."
    (is (= 1 (balls-a-frame [10 3 4 5])))
    (is (= 2 (balls-a-frame [7 3 4 5])))
    (is (= 2 (balls-a-frame [5 3 4 5])))))

(deftest final-frame-scores-test
  (testing "Count the final score of each frame."
    (is (= [20 17 9 20 30 22 15 5 17 13 3 0]
           (final-frame-scores (flatten [[10]
                                  [7 3]
                                  [7 2]
                                  [9 1]
                                  [10]
                                  [10]
                                  [10]
                                  [2 3]
                                  [6 4]
                                  [7 3 3]]))))))

(deftest final-score-test
  (testing "Count the final score."
    (is (= 168
           (final-score [20 17 9 20 30 22 15 5 17 13 3 0])))))

(deftest check-complete-test
  (testing "Check if the game is compelte."
    (is (= 168
           (check-complete [["x"]
                           ["7" "/"]
                           ["7" "2"]
                           ["9" "/"]
                           ["x"]
                           ["x"]
                           ["x"]
                           ["2" "3"]
                           ["6" "/"]
                           ["7" "/" "3"]])))
    (is (= nil
           (check-complete [["x"]
                           ["7" "/"]
                           ["7" "2"]
                           ["9" "/"]
                           ["x"]
                           ["x"]
                           ["x"]
                           ["2" "3"]
                           ["6" "/"]])))))
