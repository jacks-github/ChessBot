
# a list of files containing large dumps of pgn data.
# this data is used to create the opening book
foldername = "Lichess Elite Database"
filenames = [ "lichess_elite_2020-01.pgn", "lichess_elite_2020-02.pgn", "lichess_elite_2020-03.pgn", "lichess_elite_2020-04.pgn", "lichess_elite_2020-05.pgn" ]

# the minimum number of games a move must be played in to add it to the opening book
min_games_for_move_consideration = 20

# a tree of nodes is used to store the moves made at each ply and the number of occurences of each move
class Node :

    def __init__(self, parent, move) :
        self.parent = parent
        self.move = move
        self.children = []
        self.popularity = 1

    def __str__(self) :
        return self.move + " : " + str(self.popularity)

    def get_child_by_move(self, move) :
        for child in self.children :
            if child.move == move :
                return child
        return None
    
    def get_child_moves(self) :
        moves = []
        for child in self.children :
            moves.append(child.move)
        return moves

    def print_tree(self, depth) :
        for child in self.children :
            print(" " * depth + str(child))
            child.print_tree(depth + 1)
            
    def print_to_file(self, depth, file) :
        for child in self.children :
            file.write(" " * depth + child.move + "\n")
            child.print_to_file(depth + 1, file)

    def prune(self, threshold) :
        for child in self.children :
            if child.popularity < threshold :
                self.children.remove(child)
        for child in self.children :
            child.prune(threshold)

    def sort_by_popularity(self) :
        self.children.sort(key=lambda child: child.popularity, reverse=True)
        
        for child in self.children :
            child.sort_by_popularity()
            
rootnode = Node(None, "")

for filename in filenames :
    file = open(foldername + "/" + filename, "r")
    print("parsing", filename)
    linenumber = 0
    linereport = 0
    for line in file :
        linenumber += 1
        linereport += 1
        # ignore metadata (where and when the game was played, etc.)
        if not line.startswith("1.") :
            continue

        # get list of moves played in each game as an array of strings
        game = []
        while not line == "\n" :
            if line.endswith("\n") :
                line = line[:-1]
            game.extend(line.split(" "))
            line = file.readline()
            linenumber += 1
            linereport += 1

        # remove comments
        iscomment = False
        for move in game :
            if move.startswith("{") :
                iscomment = True
            if move.endswith("}") :
                iscomment = False
            if iscomment :
                game.remove(move)

        # remove move numbers and notation to say who won the game
        for move in game :
            if move.endswith(".") :
                game.remove(move)
            if move == "1-0" :
                game.remove(move)
            if move == "0-1" :
                game.remove(move)
            if move == "1/2-1/2" :
                game.remove(move)
            if move == "*" :
                game.remove(move)

        # add the game to the tree
        currentnode = rootnode
        for move in game :
            if move not in currentnode.get_child_moves() :
                childnode = Node(currentnode, move)
                currentnode.children.append(childnode)
                currentnode = childnode
            else :
                currentnode = currentnode.get_child_by_move(move)
                currentnode.popularity += 1

        # print progress (so the user doesn't think the application has crashed)
        if linereport > 10000 :
            print("parsed up to line", linenumber)
            linereport -= 10000
        
    file.close()

    print("pruning move tree...")
    rootnode.prune(min_games_for_move_consideration)

# sort the tree
rootnode.sort_by_popularity()

# write the tree to a file

print("writing opening book to file...")
file = open("opening_book", "w")
rootnode.print_to_file(0, file)
file.close();
print("done")
