(ns bowling-scorecard.core-test
  (:require [clojure.test :refer :all]
            [bowling-scorecard.core :refer :all]))

(deftest new-scorecard-test
  (testing "creating a new empty scorecard"
    (is (= [] (new-scorecard)))))

(deftest intize-ball-test
  (testing "Transform a ball to an int or keyword."
    (is (= :strike (intize-ball "x")))
    (is (= :spare (intize-ball "/")))
    (is (= 3 (intize-ball "3")))
    (is (= nil (intize-ball nil)))
    (is (thrown? Exception (intize-ball "14")))
    (is (thrown? Exception (intize-ball 9)))
    (is (thrown? Exception (intize-ball "a")))))

(deftest check-valid-test
  (testing "Check the three-ball input is legal format"
    (is (= [10 10 10] (check-valid :strike :strike :strike 9)))
    (is (= [10 10 3] (check-valid :strike :strike 3 9)))
    (is (= [10 3 3 ] (check-valid :strike 3 3 9)))
    (is (= [10 4 6] (check-valid :strike 4 :spare 9)))
    (is (= [8 2 8] (check-valid 8 :spare 8 9)))
    (is (= [8 2 10] (check-valid 8 :spare :strike 9)))
    (is (= nil (check-valid :strike 8 2 9)))
    (is (= nil (check-valid :strike :spare 2 9)))
    (is (= nil (check-valid :strike :spare :spare 9)))
    (is (= nil (check-valid 9 :strike 8 9)))
    (is (= nil (check-valid :srike :strike :strike 8))))
  (testing "Check the two-ball input is legal format"
    (is (= [8 2] (check-valid 8 :spare nil 0)))
    (is (= [8 1] (check-valid 8 1 nil 1)))
    (is (= [0 0] (check-valid 0 0 nil 2)))
    (is (= [0 10] (check-valid 0 :spare nil 2)))
    (is (= [1 8] (check-valid 1 8 nil 8)))
    (is (= [2 0] (check-valid 2 0 nil 7)))
    (is (= [2 0] (check-valid 2 0 nil 9)))
    (is (= nil (check-valid :strike 8 nil 0)))
    (is (= nil (check-valid :strike :spare nil 1)))
    (is (= nil (check-valid :spare :spare nil 2)))
    (is (= nil (check-valid 9 :strike nil 4)))
    (is (= nil (check-valid 9 :spare nil 9))))
  (testing "Check the one-ball input is legal format"
    (is (= [10] (check-valid :strike nil nil 8)))
    (is (= nil (check-valid :spare nil nil 8)))
    (is (= nil (check-valid 2 nil nil 7)))
    (is (= nil (check-valid :strike nil nil 9)))
    (is (= nil (check-valid 2 nil nil 9)))
    (is (= nil (check-valid 0 nil nil 9)))))

(deftest score-a-frame-test
  (testing "Score the first 9 frame."
    (is (= [[10]] (score-a-frame [] "x")))
    (is (= [[7 3]] (score-a-frame [] "7" "/")))
    (is (= [[7 2]] (score-a-frame [] "7" "2")))
    (is (thrown? Exception  (score-a-frame [] "7")))
    (is (thrown? Exception  (score-a-frame [] "a")))
    (is (thrown? Exception  (score-a-frame [] 0)))
    (is (thrown? Exception  (score-a-frame [] "7" "3")))
    (is (thrown? Exception  (score-a-frame [] "/" "3")))
    (is (thrown? Exception  (score-a-frame [] "x" "3")))
    (is (thrown? Exception  (score-a-frame [] "x" "/")))
    (is (thrown? Exception  (score-a-frame [] "a" "3")))
    (is (thrown? Exception  (score-a-frame [] 0 "3")))
    (is (thrown? Exception  (score-a-frame [] "7" "/" "3"))))
  (testing "Score the last frame."
    (is (= [[10] [10] [10] [10] [10] [10] [10] [10] [10] [2 7]] (score-a-frame [[10] [10] [10] [10] [10] [10] [10] [10] [10]] "2" "7")))
    (is (= [[10] [10] [10] [10] [10] [10] [10] [10] [10] [7 3 3]] (score-a-frame [[10] [10] [10] [10] [10] [10] [10] [10] [10]] "7" "/" "3")))
    (is (= [[10] [10] [10] [10] [10] [10] [10] [10] [10] [10 7 3]] (score-a-frame [[10] [10] [10] [10] [10] [10] [10] [10] [10]] "x" "7" "/")))
    (is (thrown? Exception (score-a-frame [[10] [10] [10] [10] [10] [10] [10] [10] [10]] "3")))
    (is (thrown? Exception (score-a-frame [[10] [10] [10] [10] [10] [10] [10] [10] [10]] "x")))
    (is (thrown? Exception (score-a-frame [[10] [10] [10] [10] [10] [10] [10] [10] [10]] "a")))
    (is (thrown? Exception (score-a-frame [[10] [10] [10] [10] [10] [10] [10] [10] [10]] "/")))
    (is (thrown? Exception (score-a-frame [[10] [10] [10] [10] [10] [10] [10] [10] [10]] 0)))
    (is (thrown? Exception (score-a-frame [[10] [10] [10] [10] [10] [10] [10] [10] [10]] "3" "7")))
    (is (thrown? Exception (score-a-frame [[10] [10] [10] [10] [10] [10] [10] [10] [10]] "7" "/")))
    (is (thrown? Exception (score-a-frame [[10] [10] [10] [10] [10] [10] [10] [10] [10]] "x" "x" "/")))
    (is (thrown? Exception (score-a-frame [[10] [10] [10] [10] [10] [10] [10] [10] [10]] "2" "7" "/")))
    (is (thrown? Exception (score-a-frame [[10] [10] [10] [10] [10] [10] [10] [10] [10]] "2" "7" "x")))
    (is (thrown? Exception (score-a-frame [[10] [10] [10] [10] [10] [10] [10] [10] [10]] "3" "7" "/")))))

(deftest strike?-test
  (testing "check if is a strike"
    (is (= true (strike? [10 3 4])))
    (is (= true (strike? [10])))
    (is (= false (strike? [7 3 4])))
    (is (= false (strike? [5 3])))))

(deftest spare?-test
  (testing "check if is a spare"
    (is (= true (spare? [7 3 4])))
    (is (= true (spare? [6 4])))
    (is (= false (spare? [10 3 4])))
    (is (= false (spare? [5 3])))
    (is (= false (spare? [10])))))

(deftest final-frame-scores-test
  (testing "Count the final score of each frame."
    (is (= [20 17 9 20 30 22 15 5 17 13]
           (final-frame-scores [[10]
                                [7 3]
                                [7 2]
                                [9 1]
                                [10]
                                [10]
                                [10]
                                [2 3]
                                [6 4]
                                [7 3 3]])))))

(deftest final-score-test
  (testing "Count the final score."
    (is (= 168
           (final-score [20 17 9 20 30 22 15 5 17 13])))))

(deftest check-complete-test
  (testing "Check if the game is compelte."
    (is (= 168
           (check-complete [[10]
                           [7 3]
                           [7 2]
                           [9 1]
                           [10]
                           [10]
                           [10]
                           [2 3]
                           [6 4]
                           [7 3 3]])))
    (is (= nil
           (check-complete [[10]
                           [7 3]
                           [7 2]
                           [9 1]
                           [10]
                           [10]
                           [10]
                           [2 3]
                           [6 4]])))))
