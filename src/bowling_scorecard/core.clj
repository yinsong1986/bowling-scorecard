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
    (contains? #{"0" "1" "2" "3" "4" "5" "6" "7" "8" "9"} ball) (read-string ball)
    :else (throw (Exception. "Illeagal result of balls."))))

(defn check-valid
  "Check if the balls are valid for a frame, return valid ints or nil"
  [result_of_ball_1 result_of_ball_2 result_of_ball_3 number_of_finished_frames]
  (cond
    ;; first 9 frames
    (< number_of_finished_frames 9) (cond
                                      ;; first strike, second nil, third nil
                                      (and (= result_of_ball_1 :strike) (= nil result_of_ball_2) (= nil result_of_ball_3)) [10] 
                                      ;; first open frame, third nil
                                      (and 
                                        (integer? result_of_ball_1)
                                        (= nil result_of_ball_3)) (cond
                                                                    ; second spare
                                                                    (= result_of_ball_2 :spare) [result_of_ball_1 (- 10 result_of_ball_1)]
                                                                    ; second open frame
                                                                    (and
                                                                      (integer? result_of_ball_2)
                                                                      (< (+ result_of_ball_1 result_of_ball_2) 10)) [result_of_ball_1 result_of_ball_2]
                                                                    ; other not valid
                                                                    :else nil)
                                      ;; others not valid
                                      :else nil)
    ;; last frame
    (= number_of_finished_frames 9) (cond
                                      ;; first strike
                                      (= result_of_ball_1 :strike) (cond
                                                                     ;; second strike
                                                                     (= result_of_ball_2 :strike) (cond
                                                                                                    ; third strike
                                                                                                    (= result_of_ball_3 :strike) [10 10 10]
                                                                                                    ; third open frame
                                                                                                    (integer? result_of_ball_3) [10 10 result_of_ball_3]
                                                                                                    ; others not valid
                                                                                                    :else nil)
                                                                     ;; second open frame
                                                                     (integer? result_of_ball_2) (cond
                                                                                                   ; third spare
                                                                                                   (= result_of_ball_3 :spare) [10 result_of_ball_2 (- 10 result_of_ball_2)]
                                                                                                   ; third open frame
                                                                                                   (and
                                                                                                     (integer? result_of_ball_3)
                                                                                                     (< (+ result_of_ball_2 result_of_ball_3) 10)) [10 result_of_ball_2 result_of_ball_3]
                                                                                                   ; others not valid
                                                                                                   :else nil)
                                                                     ;; other not valid
                                                                     :else nil)
                                      ;; first open frame
                                      (integer? result_of_ball_1) (cond
                                                                   ;; second spare
                                                                   (= result_of_ball_2 :spare) (cond
                                                                                                 ; third open frame
                                                                                                 (integer? result_of_ball_3) [result_of_ball_1 (- 10 result_of_ball_1) result_of_ball_3]
                                                                                                 ; third strike
                                                                                                 (= result_of_ball_3 :strike) [result_of_ball_1 (- 10 result_of_ball_1) 10]
                                                                                                 ; others not valid
                                                                                                 :else nil)
                                                                   ;; second open frame, third nil
                                                                   (and
                                                                     (integer? result_of_ball_2)
                                                                     (= nil result_of_ball_3)
                                                                     (< (+ result_of_ball_1 result_of_ball_2) 10)) [result_of_ball_1 result_of_ball_2]
                                                                   ;; others not valid
                                                                   :else nil)
                                      ;; others not valid
                                      :else nil)
    ;; others not valid
    :else nil))

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
