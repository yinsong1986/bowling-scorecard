(ns bowling-scorecard.core
  (:gen-class))

(defn new-scorecard
  "Create an empty score card"
  []
  (vector))

(defn score-a-frame
  "Given a score card, score a frame"
  ;; three balls for special last frame
  ([score_card, result_of_ball_1, result_of_ball_2, result_of_ball_3]
     (if (and (= "x" result_of_ball_1) (= 9 (count score_card)))
       (conj score_card [result_of_ball_1 result_of_ball_2 result_of_ball_3])
       nil))
  ;; two balls for other cases
  ([score_card, result_of_ball_1, result_of_ball_2]
     (conj score_card [result_of_ball_1 result_of_ball_2])))

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
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
