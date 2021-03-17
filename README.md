# Jeff bot

Jeff bot is a chess bot coded in java. If you have a good CPU and at least 8 Gb of memory, you can play against Jeff by dowloading the [jar file](https://github.com/BorysSerbyn/Jeff-bot/tree/master/out/artifacts/chess_jar).

![screenshot](https://i.imgur.com/MEmtkXk.jpg)

The basic idea behind Jeff's algorithm is that it creates a tree of boards stemming out of the current game configuration. Then, it scores the boards using a scoring function with many variables and cascades the results back down the tree with a min max algorithm.

To speed things up, I implemented alpha beta pruning, which prunes branches if their current score is less relevant than those of other branches on the same layer. I've also implemented parallelization by multi threading the recursive calls up to a depth of 2. This optimization is loosely inspired by some of the techniques used by deep blue.

At this moment, Jeff can outperform stock fish level 3 (lichess' chess engine). Ultimately, I'd like for Jeff to be able to beat level 5.

![diagram](/docs/tree building sequence diagram.png)
