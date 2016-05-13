# bowling-scorecard

Build an API that implements a ten-pin bowling scorecard (see also the wikipedia page).

## Getting Started

Download the project from github

    git clone https://github.com/yinsong1986/bowling-scorecard.git

Run unit tests

    cd bowling-scorecard
    lein test

You should see the following results.

   lein test bowling-scorecard.core-test

   Ran 11 tests containing 75 assertions.
   0 failures, 0 errors.

Run Demo

    lein run

You should see results as below.

    Creating a new score card
    []
    Add the 1st frame
    [[x]]
    Add the 2nd frame
    [[x] [7 /]]
    Add the 3rd frame
    [[x] [7 /] [7 2]]
    Add the 4th frame
    [[x] [7 /] [7 2] [9 /]]
    Add the 5th frame
    [[x] [7 /] [7 2] [9 /] [x]]
    Add the 6th frame
    [[x] [7 /] [7 2] [9 /] [x] [x]]
    Add the 7th frame
    [[x] [7 /] [7 2] [9 /] [x] [x] [x]]
    Add the 8th frame
    [[x] [7 /] [7 2] [9 /] [x] [x] [x] [2 3]]
    Add the 9th frame
    [[x] [7 /] [7 2] [9 /] [x] [x] [x] [2 3] [6 /]]
    Add the 10th frame
    [[x] [7 /] [7 2] [9 /] [x] [x] [x] [2 3] [6 /] [7 / 3]]
    Show the final score:
    168
   
## Examples of using the APIs

1. Create an empty score card

    (new-scorecard) ;-> []

2. Given a score card, score a frame

    (def score_card (new-scorecard)) ; create a new scorecard
    (score-a-frame score_card "x") ;-> [["x"]]
    (score-a-frame score_card "7" "/") ;-> [["7" "/"]]
    (score-a-frame score_card "7" "2") ;-> [["7" "2"]]

3. Determine whether a game is complete - if so, provide the final score

    (def score_card [["x"] ["7" "/"] ["7" "2"] ["9" "/"] ["x"] ["x"] ["x"] ["2" "3"] ["6" "/"] ["7" "/" "3"]])
    (check-complete score_card) ;-> 168

For more details for usage, please refer to the demo function in src/bowling_scorecard/core.clj/-main.

## License

Copyright © 2016 Yin Song

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
