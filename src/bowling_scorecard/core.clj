(ns bowling-scorecard.core
  (:gen-class))
;;; 1. Create an empty score card
(defn new-scorecard
  "Create an empty score card"
  []
  (vector))

;;; 2. Given a score card, score a frame
(defn check-input
  "Check the input of the ball results."
  ;; three balls
  ([result_of_ball_1 result_of_ball_2 result_of_ball_3]
    (if (and (string? result_of_ball_1)
          (string? result_of_ball_2)
          (string? result_of_ball_3)) ;; check if they are string
      (if (and (re-matches #"[0-9]|x|/" result_of_ball_1)
            (re-matches #"[0-9]|x|/" result_of_ball_2)
            (re-matches #"[0-9]|x|/" result_of_ball_3)) ;; check if string are [0-9] x /
        (cond
          (re-matches #"x" result_of_ball_1) (cond ; first strike
                                               (and (re-matches #"x" result_of_ball_2)
                                                 (re-matches #"[0-9]|x" result_of_ball_3)) true 
                                               (and (re-matches #"[0-9]" result_of_ball_2)
                                                 (re-matches #"/" result_of_ball_3)) true
                                               (and (re-matches #"[0-9]" result_of_ball_2)
                                                 (re-matches #"[0-9]" result_of_ball_3)
                                                 (< (+ (read-string result_of_ball_2) (read-string result_of_ball_3)) 10)) true
                                               :else (throw (Exception. "Illeagal result of balls.")))
          (and (re-matches #"[0-9]" result_of_ball_1) (re-matches #"/" result_of_ball_2) (re-matches #"[0-9]|x" result_of_ball_3)) true ; first spare
          :else (throw (Exception. "Illeagal result of balls."))      
        )
        (throw (Exception. "The result must be string 0-9, x, /")))
      (throw (Exception. "The results of balls must be string."))) 
  )
  ;; two balls
  ([result_of_ball_1 result_of_ball_2]
    (if (and (string? result_of_ball_1)
          (string? result_of_ball_2)) ;; check if they are string
      (if (and (re-matches #"[0-9]|x|/" result_of_ball_1)
            (re-matches #"[0-9]|x|/" result_of_ball_2)) ;; check if string are [0-9] x /
        (cond
          (and (re-matches #"[0-9]" result_of_ball_1)
            (re-matches #"/" result_of_ball_2)) true ; spare
          (and (re-matches #"[0-9]" result_of_ball_1)
            (re-matches #"[0-9]" result_of_ball_2)
            (< (+ (read-string result_of_ball_1) (read-string result_of_ball_2)) 10)) true ; open frame
          :else (throw (Exception. "Illeagal result of balls."))
        )
        (throw (Exception. "The result must be string 0-9, x, /")))
      (throw (Exception. "The results of balls must be string.")))
  )
  ;; one ball
  ([result_of_ball_1]
    (if (string? result_of_ball_1) ;; check if they are string 
      (if (re-matches #"x" result_of_ball_1);; check if string is x, only strike is legal
        true
        (throw (Exception. "The result must be string x")))
      (throw (Exception. "The results of balls must be string."))) 
  )
)

(defn score-a-frame
  "Given a score card, record a frame"
  ;; three balls for special last frame
  ([score_card result_of_ball_1 result_of_ball_2 result_of_ball_3]
     (if (and (check-input result_of_ball_1 result_of_ball_2 result_of_ball_3)
           (= 9 (count score_card)))
       (conj score_card [result_of_ball_1 result_of_ball_2 result_of_ball_3])
       (throw (Exception. "Illeagal result of balls.."))))
  ;; two balls for other cases
  ([score_card result_of_ball_1 result_of_ball_2]
     (if (check-input result_of_ball_1 result_of_ball_2)
       (if (and (re-matches #"[0-9]" result_of_ball_1)
             (re-matches #"/" result_of_ball_2)
             (= 9 (count score_card))) ; Special treatment for last frame
         (throw (Exception. "Illegal last two-ball frame."))
         (conj score_card [result_of_ball_1 result_of_ball_2]))
       (throw (Exception. "Unknown exception."))))
  ([score_card result_of_ball_1 ]
     (if (check-input result_of_ball_1)
       (if (= 9 (count score_card)) ; Special treatment for last frame
         (throw (Exception. "Illegal last one-ball frame."))
         (conj score_card [result_of_ball_1]))
       (throw (Exception. "Unknown exception..")))))

;;; 3. Determine whether a game is complete - if so, provide the final score.
(defn intize-balls
  "Change the raw score format of balls in a frame, e.g., x, / to integer score"
  [balls]
  (remove
    nil?
    (vector 
      (if (= (get balls 0) "x") 10 (read-string (get balls 0)))
      (if (= (get balls 1) nil)
        nil
        (cond
          (= (get balls 1) "x") 10
          (= (get balls 1) "/") (- 10 (read-string (get balls 0)))
          :else (read-string (get balls 1))))
      (if (= (get balls 2) nil)
        nil
        (cond
          (= (get balls 2) "x") 10
          (= (get balls 2) "/") (- 10 (read-string (get balls 1)))
          :else (read-string (get balls 2)))))))

(defn strike? [balls]
  "Check if it is a strike."
  (= 10 (first balls)))

(defn spare? [balls]
  "Check if it is a spare."
  (= 10 (apply + (take 2 balls))))

(defn balls-a-frame-score
  "The number of balls contribute to a frame's score."
  [balls]
  (cond
   (strike? balls) 3
   (spare? balls) 3
   :else 2))

(defn balls-a-frame
  "The number of balls in a frame"
  [balls]
  (if (strike? balls) 1 2))

(defn final-frame-scores
  "Count the final scores of each frame"
  [balls_seq]
  (if (empty? balls_seq)
    [0]
    (concat (vector (reduce + (take (balls-a-frame-score balls_seq) balls_seq)))
      (final-frame-scores (drop (balls-a-frame balls_seq) balls_seq)))))

(defn final-score
  "Count the overall score"
  [frame_scores]
  (reduce + (take 10 frame_scores)))

(defn check-complete
  "Determine whether a game is complete. If so, provide the final score"
  [score_card]
  (if (= 10 (count score_card))
    (final-score
      (final-frame-scores
        (flatten (map intize-balls score_card))))
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
