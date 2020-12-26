# ChessBot

ChessBot is a free and open source chess engine written in Java.

## Running ChessBot

For convenience, the software is presented in the form of an Eclipse project. The easiest way to run ChessBot is to clone the git repository and simply import that folder into Eclipse.

You are free to use this software however you want, including modifying it. If you do so, I would be interested to see your results. If you make an improvement or optimisation, please submit a pull request.

## ChessBot AI Types

This software looks to implement incrementally sophisticated approaches to chess AI, both as a learning tool and as an experiment in performance profiling. These AI types are enumerated below:

### BruteForce Minimax

This AI looks at all possible moves for the turn player, then all the possible moves that the opponent could make in response to that move.
It then looks at all possible moves the turn player could make in response to that move, and so on and so on.
This approach, while accurate, is incredibly inefficient. It can take upwards of a minute to calculate a single move at a depth of 5 plies, even using multiple threads.
For this reason, some improvements are clearly necessary.

### AlphaBeta MiniMax

When traversing a tree of possible moves using MiniMax, it is not necessary to look at every single possible move to establish which move is best.
If at any point we discover that the branch we are searching cannot possibly be better than a previously searched branch, we do not care *how much* worse that branch is. We can immediately discard it from the search.
Additionally, we do not care whether there are even worse moves possible further down that branch. In other words, a single refutation should be enough to discard a branch from the search.
Interestingly, Alpha-Beta pruned searches work much more efficiently when the most promising candidate moves are evaluated first.
For this reason the list of moves is sorted before traversal with preference given to active moves such as attacks, checks or captures.

You can read more about Alpha-Beta Pruning at [https://www.chessprogramming.org/Alpha-Beta].

### AlphaBeta MiniMax w/ Opening Book

ChessBot is not yet optimised to analyse the boardstate to a very deep number of plies. For that reason, it makes sense to use a database for the opening section of the game.
The best responses to each move can be calculated ahead of time and the engine will then play as though it could calculate much deeper.

To create such a database, I wrote a small python program which can be found in the utils folder of the git repository.
This program parses large amounts of PGN data to find the most popular responses to each move. It then creates an opening book in a format which can then be read by ChessBot.
The win percentage of each move is not taken into account, only the popularity of each move is used to create this database.

The opening book file supplied with this program was built using data from the Lichess Elite Database, which you can find here: [https://database.nikonoel.fr/].
This database was compiled by Lichess user nikonoel [https://lichess.org/@/nikonoel]. It contains data from games played by players rated 2400 Elo or higher versus players rated at least 2200 on [https://lichess.org].

Of the games from this database, my python program considered games played between January and May 2020. This section of the database contains around 1.6 million games of chess.
More games would of course result in a better opening database, but this is probably enough for the time being.

A move was added to the database if it had been played at least twenty times among the games in a specific pgn file (i.e. one month of chess games).
The number twenty is completely arbitrary; I feel that this number ought to be played with and a more intelligent pruning method found. I expect the best way to do that would be to look at game win percentages.
Using a threshold in this way served two purposes:
* Excluding novelties and unsound lines
* Reducing the amount of memory needed to compute the list

When I ran the program leaving the tree unpruned after parsing each pgn file, the program's memory usage quickly exceeded 15GB.
I expect that to consider more than 5 months worth of games, a computer with more than 16GB of memory would be needed.
Either that or it would be necessary to write the program in such a way that it swaps memory on and off the disk.

In any case, the resulting tree of opening moves is stored as a plain text file with move hierarchy indicated by indentation.
The moves are ordered from most to least common, so for any given position we can simply pick the most popular continuation played in elite level games.

This database my program has come up with so far is a reasonable approximation of the databases commonly found on large chess websites such as [https://chess.com].
However sometimes it prefers slightly uncommon lines, for example it deviates from the mainline of the sicilian found in most databases at move 6, preferring 6. Bg5 for white over the more common 6. Be3.
This line is still absolutely playable at grandmaster level, but I would prefer my engine be as accurate as possible. To do that I would need to use more data from a wider selection of games.

In the future I intend to refine the database further. For now, though, I am certain that using the database will on average increase the strength of the engine.
