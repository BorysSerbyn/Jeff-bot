# Jeff bot

Jeff bot is a chess bot coded in java. You can play against Jeff by dowloading the [jar file](https://github.com/BorysSerbyn/Jeff-bot/tree/master/out/artifacts/chess_jar).

The basic idea behind Jeff's algorithm is that it creates a tree of boards stemming out of the current game configuration. It scores the nodes using a minimax algorithm.

At the moment, im pruning the tree using a very crude approach (see fig. 1). I intend to replace it with proper alpha beta pruning as illustrated in fig. 2.

At the moment, jeff can beat stockfish level 4 50% of the time at 4 seconds calculation time.

![fig. 1](/docs/tree%20building%20sequence%20diagram.png)

![fig. 2](/docs/alpha%20beta%20search%20sequence%20diagram.png)
