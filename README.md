# tablut
## Game Rules 
The objective of the white pieces is to get the King piece to the edge of the board. 
The black pieces try to capture the king before it reaches the edge. The 16 black pieces 
start the game first. The white pieces, with the king at the center of the board, move next and 
the two sides alternate turns. All pieces move chess rooks, any number of squares vertically or horizontally.
No piece can occupy the same square as another piece and no piece can be 'jumped'. No piece other 
than the kind may land on the middle square. There are several conditions for capturing an enemy but 
the most common scenario occurs when an enemy's move results in the piece being enclosed on two opposite
sides. A king is captured like any other piece except for when it is on the center piece or any of the four
orthogonally adjacent squares. In this situation the king must be surrounded on all four sides by enemy pieces.

## Game Design
The first step of the project was to design the the board and the neccessary data structures to store the information
about the current state of the board, the pieces, and the possible moves. Then all the rules for the game were implemented.
The interesting part of the project was designing the AI agent. This was done by using the Minimax algorithm and 
alpha beta pruning. Essentially what happens is a the current state of the board is sent to a heursitic function
which returns a low score if the board is favorable to the black player and a high score if favorable to the 
white player. This heursitic function takes into account the location of the king and the number of pieces when 
determing the score of a move. If the move results in a win the score is set to a max score and vice versa.
Then a game tree is constucted such that the edges represent a move and the nodes represent the score
of that move. The depth of the tree is predetermined. When the depth has been reached or a node is a winning move
then the optimal move is selected. Using this algorithm, the AI is able to detect a win that is within 4 moves.
