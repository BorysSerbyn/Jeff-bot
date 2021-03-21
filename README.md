# Jeff bot

Jeff is a chess bot coded in java. You can play against Jeff by dowloading this [jar file](https://github.com/BorysSerbyn/Jeff-bot/tree/master/out/artifacts/Jeff_bot_jar/Jeff-bot.jar).

The recursive function in the bot uses a minimax algorithm and alpha beta pruning to create a tree of positions stemming from the current position (see fig.1).

At the moment, jeff can beat stockfish level 4 50% of the time. It spends around 7 seconds per move (your mileage may vary).

![fig. 1](/docs/alpha%20beta%20search%20sequence%20diagram.png)
