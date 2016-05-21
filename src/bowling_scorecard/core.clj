(ns bowling-scorecard.core
  (:gen-class))
;;; 1. Create an empty score card
(defn new-scorecard
  "Create an empty score card"
  []
  (vector))

;;; 2. Given a score card, score a frame
(defn intize-ball
  "Transform ball to int or keyword, e.g., x, / to :strike, :spare"
  [ball]
  (cond
    (= nil ball) nil ; dummy ball
    (= "x" ball) :strike
    (= "/" ball) :spare
    (or
      (= "0" ball)
      (= "1" ball)
      (= "2" ball)
      (= "3" ball)
      (= "4" ball)
      (= "5" ball)
      (= "6" ball)
      (= "7" ball)
      (= "8" ball)
      (= "9" ball)) (read-string ball)
    :else (throw (Exception. "Illeagal result of balls."))))

(defn check-valid
  "Check if the balls are valid for a frame, return valid ints or nil"
  [result_of_ball_1 result_of_ball_2 result_of_ball_3 number_of_finished_frames]
  (cond
    ;; One ball
    (and
      (= false (nil? result_of_ball_1))
      (= true (nil? result_of_ball_2))
      (= true (nil? result_of_ball_3))) (if (and ; is strike and not the last frame
                                              (= result_of_ball_1 :strike) ;
                                              (< number_of_finished_frames 9))
                                          [10]
                                          nil)
    ;; Two balls
    (and 
      (= false (nil? result_of_ball_1))
      (= false (nil? result_of_ball_2))
      (= true (nil? result_of_ball_3))) (cond
                                          ; spare and not the last frame
                                          (and
                                            (integer? result_of_ball_1)
                                            (= result_of_ball_2 :spare)
                                            (< number_of_finished_frames 9)) [result_of_ball_1 (- 10 result_of_ball_1)]
                                          ; open frame
                                          (and
                                            (integer? result_of_ball_1)
                                            (integer? result_of_ball_2)
                                            (< (+ result_of_ball_1 result_of_ball_2) 10)) [result_of_ball_1 result_of_ball_2]
                                          ; others not valid
                                          :else nil)
    ;; Three balls, only valid for last frame
    (and
      (= false (nil? result_of_ball_1))
      (= false (nil? result_of_ball_2))
      (= false (nil? result_of_ball_3))
      (= number_of_finished_frames 9)) (cond
                                         ; strike, strike, strike
                                         (and 
                                           (= result_of_ball_1 :strike)
                                           (= result_of_ball_2 :strike)
                                           (= result_of_ball_3 :strike)) [10 10 10]
                                         ; strike, strike, open frame
                                         (and 
                                           (= result_of_ball_1 :strike)
                                           (= result_of_ball_2 :strike)
                                           (integer? result_of_ball_3)) [10 10 result_of_ball_3]
                                         ; strike, spare
                                         (and 
                                           (= result_of_ball_1 :strike)
                                           (integer? result_of_ball_2)
                                           (= result_of_ball_3 :spare)) [10 result_of_ball_2 (- 10 result_of_ball_2)]
                                         ; strike, open frame
                                         (and 
                                           (= result_of_ball_1 :strike)
                                           (integer? result_of_ball_2)
                                           (integer? result_of_ball_3)
                                           (< (+ result_of_ball_2 result_of_ball_3) 10)) [10 result_of_ball_2 result_of_ball_3]
                                         ; spare, open frame
                                         (and
                                           (integer? result_of_ball_1)
                                           (= result_of_ball_2 :spare)
                                           (integer? result_of_ball_3)) [result_of_ball_1 (- 10 result_of_ball_1) result_of_ball_3]
                                         ; spare, strike
                                         (and
                                           (integer? result_of_ball_1)
                                           (= result_of_ball_2 :spare)
                                           (= result_of_ball_3 :strike)) [result_of_ball_1 (- 10 result_of_ball_1) 10]
                                         ; others not valid
                                         :else nil) 
    ;; Others not valid
    :else nil
 )
)

(defn score-a-frame
  "Given a score card, record a frame"
  ;; three balls for special last frame
  ([score_card result_of_ball_1 result_of_ball_2 result_of_ball_3]
    (let [valid_balls (check-valid
                        (intize-ball result_of_ball_1)
                        (intize-ball result_of_ball_2)
                        (intize-ball result_of_ball_3)
                        (count score_card))]
      (if (nil? valid_balls) 
        (throw (Exception. "Illeagal result of balls.."))
        (conj score_card valid_balls))))
  ;; two balls for other cases
  ([score_card result_of_ball_1 result_of_ball_2]
    (score-a-frame score_card result_of_ball_1 result_of_ball_2 nil))

  ;; one ball
  ([score_card result_of_ball_1]
    (score-a-frame score_card result_of_ball_1 nil nil)))

;;; 3. Determine whether a game is complete - if so, provide the final score.
(defn strike? [balls]
  "Check if it is a strike."
  (= 10 (first balls)))

(defn spare? [balls]
  "Check if it is a spare."
  (if (> (count balls) 1)
    (= 10 (apply + (take 2 balls)))
    false))

(defn nth-frame-score
  "Count the final score for the nth frame"
  [frames n]
  (let [{frame_n n
         frame_n_plus_one (inc n)
         frame_n_plus_two (+ n 2)} frames] ; Get the nth, (n+1)th, (n+2)th frame
    (if (= n 9)
      ;; for the last frame
      (reduce + frame_n)
      ;; for the first 9 frames
      (let [[ball_one ball_two] (concat frame_n_plus_one frame_n_plus_two)] ; Get related two balls for counting
        (cond
          ; for strike frame, add two balls
          (strike? frame_n) (+ 10 ball_one ball_two)
          ; for spare frame
          (spare? frame_n) (+ 10 ball_one)
          ; for open frame
          :else (reduce + frame_n))))))

(defn final-frame-scores
  "Count the final scores of each frame"
  [frames]
  (let [frames_list (repeat 10 frames) n_list (range 10)]
    (map nth-frame-score frames_list n_list)))

(defn final-score
  "Count the overall score"
  [frame_scores]
  (reduce + frame_scores))

(defn check-complete
  "Determine whether a game is complete. If so, provide the final score"
  [score_card]
  (if (= 10 (count score_card))
    (final-score (final-frame-scores score_card))
    nil))

(defn -main
  "A demo for using the APIs."
  [& args]
  (println "Creating a new score card")
  (def score_card (new-scorecard))
  (println score_card)

  (println "Add the 1st frame")
  (def score_card (score-a-frame score_card "x"))
  (println score_card)

  (println "Add the 2nd frame")
  (def score_card (score-a-frame score_card "7" "/"))
  (println score_card)

  (println "Add the 3rd frame")
  (def score_card (score-a-frame score_card "7" "2"))
  (println score_card)
  
  (println "Add the 4th frame")
  (def score_card (score-a-frame score_card "9" "/"))
  (println score_card)
  
  (println "Add the 5th frame")
  (def score_card (score-a-frame score_card "x"))
  (println score_card)  

  (println "Add the 6th frame")
  (def score_card (score-a-frame score_card "x"))
  (println score_card)
  
  (println "Add the 7th frame")
  (def score_card (score-a-frame score_card "x"))
  (println score_card)  

  (println "Add the 8th frame")
  (def score_card (score-a-frame score_card "2" "3"))
  (println score_card)

  (println "Add the 9th frame")
  (def score_card (score-a-frame score_card "6" "/"))
  (println score_card)

  (println "Add the 10th frame")
  (def score_card (score-a-frame score_card "7" "/" "3"))
  (println score_card)

  (println "Show the final score:")
  (def final_score (check-complete score_card))
  (println final_score))
