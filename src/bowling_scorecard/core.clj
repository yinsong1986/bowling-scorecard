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

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
