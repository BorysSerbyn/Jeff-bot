# Jeff bot

Jeff is a chess bot coded in java. You can play against Jeff by dowloading this [jar file](https://github.com/BorysSerbyn/Jeff-bot/tree/master/out/artifacts/Jeff_bot_jar/Jeff-bot.jar).

Although the code is not in this repository, jeff is also on lichess (https://lichess.org/@/jeff_bot), so feel free to give him a follow. Jeff isnt currently hosted anywhere, so you cant play against him on the website.

The recursive function in the bot uses a minimax algorithm and alpha beta pruning to create a tree of positions stemming from the current position (see fig.1).

At the moment, jeff can beat stockfish level 4 most of the time. It spends around 500 ms per move (your mileage may vary).

![fig. 1](/docs/alpha%20beta%20search%20sequence%20diagram.png)
