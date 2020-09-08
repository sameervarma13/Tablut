package tablut;


import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Formatter;


import static tablut.Piece.*;
import static tablut.Square.*;

/** The state of a Tablut Game.
 *  @author Sameer Varma
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 9;

    /** The throne (or castle) square and its four surrounding squares.. */
    static final Square THRONE = sq(4, 4),
        NTHRONE = sq(4, 5),
        STHRONE = sq(4, 3),
        WTHRONE = sq(3, 4),
        ETHRONE = sq(5, 4);

    /** Initial positions of attackers. */
    static final Square[] INITIAL_ATTACKERS = {
        sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
        sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
        sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
        sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };

    /** Initial positions of defenders of the king. */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
        sq(4, 6), sq(4, 2), sq(2, 4), sq(6, 4)
    };

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        _board = new Piece[9][9];
        _actionStack = new Stack<>();
        _boardStates = new ArrayList<>();
        _turn = model._turn;
        _winner = model._winner;
        _lim = model._lim;
        _moveCount = model._moveCount;
        _repeated = model._repeated;
        _boardStates.addAll(model._boardStates);
        _actionStack.addAll(model._actionStack);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                _board[i][j] = model._board[i][j];
            }
        }
    }
    /** Clears the board to the initial position. */
    void init() {
        _winner = null;
        _lim = 0;
        setMoveLimit(_lim);
        _moveCount = 0;
        _board = new Piece[9][9];
        _turn = BLACK;
        _boardStates = new ArrayList<String>();
        _actionStack = new Stack<>();
        for (Square s : INITIAL_ATTACKERS) {
            _board[s.row()][s.col()] = BLACK;
        }
        for (Square s : INITIAL_DEFENDERS) {
            _board[s.row()][s.col()] = WHITE;
        }
        _board[4][4] = KING;
        for (int i = 0; i < _board.length; i++) {
            for (int j = 0; j < _board.length; j++) {
                if (_board[i][j] == null) {
                    _board[i][j] = EMPTY;
                }
            }
        }
        _boardStates.add(encodedBoard());

    }

    /** Set the move limit to LIM.  It is an error if 2*LIM <= moveCount().
     * @param n */
    void setMoveLimit(int n) {
        _lim = n;
    }

    /** Return a Piece representing whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the winner in the current position, or null if there is no winner
     *  yet. */
    Piece winner() {
        return _winner;
    }

    /** Returns true iff this is a win due to a repeated position. */
    boolean repeatedPosition() {
        return _repeated;
    }

    /** Record current position and set winner() next mover if the current
     *  position is a repeat. */
    private void checkRepeated() {
        if (_boardStates.contains(encodedBoard())) {
            _repeated = true;
        }
        if (repeatedPosition()) {
            _winner = _turn;
        }
    }

    /** Return the number of moves since the initial position that have not been
     *  undone. */
    int moveCount() {
        return _moveCount;
    }

    /** Return location of the king. */
    Square kingPosition() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (_board[i][j] == KING) {
                    return Square.sq(j, i);
                }
            }
        }
        return null;
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return _board[row][col];
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        _board[s.row()][s.col()] = p;
    }

    /** Set square S to P and record for undoing. */
    final void revPut(Piece p, Square s) {
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /** Return true iff FROM - TO is an unblocked rook move on the current
     *  board.  For this to be true, FROM-TO must be a rook move and the
     *  squares along it, other than FROM, must be empty. */
    boolean isUnblockedMove(Square from, Square to) {
        int index = from.index();
        int dir = from.direction(to);
        if (!from.isRookMove(to)) {
            return false;
        }
        if (get(to) != EMPTY) {
            return false;
        }
        for (Square  s : ROOK_SQUARES[index][dir]) {
            if (get(s) != EMPTY) {
                return false;
            }
            if (s == to) {
                return true;
            }
        }
        return true;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return get(from).side() == _turn;
    }

    /** Return true iff FROM-TO is a valid move. */
    boolean isLegal(Square from, Square to) {
        if (!isLegal(from)) {
            return false;
        }
        if (get(to) != EMPTY) {
            return false;
        }
        if (!isUnblockedMove(from, to)) {
            return false;
        }

        return true;
    }

    /** Return true iff FROM-TO is a valid move FROM. TO. */
    boolean isLegalMoves(Square from, Square to) {
        return isUnblockedMove(from, to);
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to());
    }

    /** Move FROM-TO, assuming this is a legal move. */
    void makeMove(Square from, Square to) {
        assert isLegal(from, to);
        ArrayList<Action> arr = new ArrayList<>();
        if (_moveCount == (_lim * 2) - 1) {
            if (_turn == WHITE) {
                _winner = WHITE;
            } else {
                _winner = BLACK;
            }
        }
        if (!hasMove(_turn)) {
            if (_turn == WHITE) {
                _winner = BLACK;
            } else {
                _winner = WHITE;
            }
        }
        if (_board[from.row()][from.col()] == KING) {
            arr.add(new Action(from, KING));
            arr.add(new Action(to, EMPTY));
            _board[from.row()][from.col()] = EMPTY;
            _board[to.row()][to.col()] = KING;
        } else {
            arr.add(new Action(from, _turn));
            arr.add(new Action(to, EMPTY));
            _board[from.row()][from.col()] = EMPTY;
            _board[to.row()][to.col()] = _turn;
        }
        if (_turn == WHITE) {
            _turn = BLACK;
        } else {
            _turn = WHITE;
        }
        _moveCount++;
        cap(to, arr);
        checkRepeated();
        if (kingPosition() == null) {
            _winner = BLACK;
        } else if (kingPosition().isEdge()) {
            _winner = WHITE;
        }
        _boardStates.add(encodedBoard());
    }
    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }
    /** determines if a capture is possible. K. Return. */
    private boolean kingCap(Square k) {
        Square s1 = Square.sq(k.col() + 1, k.row());
        if (!(s1 != null  && (get(s1) == BLACK
                || (get(s1) == EMPTY && s1 == THRONE)))) {
            return false;
        }
        Square s2 = Square.sq(k.col() - 1, k.row());
        if (!(s2 != null && (get(s2) == BLACK
                || (get(s2) == EMPTY && s2 == THRONE)))) {
            return false;
        }
        Square s3 = Square.sq(k.col(), k.row() - 1);
        if (!(s3 != null && (get(s3) == BLACK
                || (get(s3) == EMPTY && s3 == THRONE)))) {
            return false;
        }
        Square s4 = Square.sq(k.col(), k.row() + 1);
        if (!(s4 != null && (get(s4) == BLACK
                || (get(s4) == EMPTY && s4 == THRONE)))) {
            return false;
        }
        return true;
    }
    /** determines s is on throne suite. Return. S. */
    private boolean onThrones(Square s) {
        if (s == NTHRONE || s == STHRONE
                || s == WTHRONE || s == ETHRONE || s == THRONE) {
            return true;
        }
        return false;
    }
    /** determines capturing ability. TO. A. RETURN */
    private void cap(Square to, ArrayList<Action> a) {
        Piece capturer = EMPTY;
        boolean reset = false;
        boolean reset2 = false;
        boolean reset3 = false;
        boolean captured = false;
        boolean blackWin = false;
        if (_board[4][4] == EMPTY) {
            _board[4][4] = get(to);
            reset = true;
        } else if (_board[4][4] == KING) {
            _board[4][4] = WHITE;
            if (get(to) != KING) {
                reset3 = true;
            }
            if (_turn == WHITE) {
                int count = 0;
                if (get(NTHRONE) == BLACK) {
                    count++;
                }
                if (get(STHRONE) == BLACK) {
                    count++;
                }
                if (get(ETHRONE) == BLACK) {
                    count++;
                }
                if (get(WTHRONE) == BLACK) {
                    count++;
                }
                if (count == 3) {
                    _board[4][4] = BLACK;
                    reset2 = true;
                }
                if (count == 4) {
                    blackWin = true;
                }
            }
        }
        capturer = capturer(capturer, to);
        captured = checkCap(to, false, capturer, a);
        if (!captured) {
            _actionStack.push(a);
        }
        if (blackWin) {
            _winner = BLACK;
            return;
        }
        if (reset) {
            _board[4][4] = EMPTY;
        }
        if (reset3) {
            _board[4][4] = KING;
        }
        if (reset2) {
            _board[4][4] = KING;
        }
    }
    /** TO. A. CAPTURED. CAPTURER. RETURN .*/
    private Piece capturer(Piece capturer, Square to) {
        if (get(to) == KING) {
            capturer = WHITE;
        } else if (_turn == WHITE) {
            capturer = BLACK;
        } else if (_turn == BLACK) {
            capturer = WHITE;
        }
        return capturer;
    }
    /** TO. A. CAPTURED. CAPTURER. RETURN .*/
    private boolean checkCap(Square to, boolean captured,
                             Piece capturer, ArrayList<Action> a) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Square s = Square.sq(j, i);
                if (s.isRookMove(to)) {
                    int dir = to.direction(s);
                    if (to.adjacent(s) && (get(s) == _turn)
                            || (_turn == WHITE && get(j, i) == KING)) {
                        Square s2 = to.rookMove(dir, 2);
                        if (s2 != null && get(to.between(s2)) == KING
                                && onThrones(to.between(s2))
                                && !kingCap(to.between(s2))) {
                            continue;
                        } else if (s2 != null && (get(s2) == capturer
                                || (get(to) == WHITE
                                && get(s2) == KING))) {
                            if (get(to.between(s2)) != get(to)
                                    && get(to) != EMPTY) {
                                captured = true;
                                capture(to, s2, a);
                            }
                        }
                    }
                }
            }
        }
        return captured;
    }

    /** Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     *  SQ0 and the necessary conditions are satisfied. A. SQO. SQ2. */
    private void capture(Square sq0, Square sq2, ArrayList<Action> a) {
        Square btw = sq0.between(sq2);
        a.add(new Action(btw, get(btw)));
        _actionStack.push(a);
        _board[btw.row()][btw.col()] = EMPTY;
        if (get(btw) == KING) {
            _winner = BLACK;
        }

    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_moveCount > 0) {
            undoPosition();
            _moveCount--;
        }
    }

    /** Remove record of current position in the set of positions encountered,
     *  unless it is a repeated position or we are at the first move. */
    private void undoPosition() {
        ArrayList<Action> undo = _actionStack.pop();
        for (Action a : undo) {
            put(a.getPiece(), a.getSquare());
        }
        _boardStates.remove(_boardStates.size() - 1);
        _repeated = false;
    }

    /** Clear the undo stack and board-position counts. Does not modify the
     *  current position or win status. */
    void clearUndo() {
        _boardStates.clear();

    }

    /** Return a new mutable list of all legal moves on the current board for
     *  SIDE (ignoring whose turn it is at the moment). */
    List<Move> legalMoves(Piece side) {
        ArrayList<Move> moveList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (get(j, i) == side
                        || (get(j, i) == KING && side == WHITE)) {
                    Square from = Square.sq(j, i);
                    for (int k = 0; k < 9; k++) {
                        for (int m = 0; m < 9; m++) {
                            Square to = Square.sq(m, k);
                            if (from.isRookMove(to)) {
                                if (isLegalMoves(from, to)) {
                                    Move move = Move.mv(from, to);
                                    moveList.add(move);
                                }
                            }
                        }
                    }
                }
            }
        }
        return moveList;
    }

    /** Return true iff SIDE has a legal move. */
    boolean hasMove(Piece side) {
        if (legalMoves(side).isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /** Return a text representation of this Board.  If COORDINATES, then row
     *  and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }
    /** Return the contents of _board in the order of SQUARE_LIST as a sequence
     *  of characters: the toString values of the current turn and Pieces. */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }

    /** Piece whose turn it is (WHITE or BLACK). */
    private Piece _turn;
    /** Cached value of winner on this board, or null if it has not been
     *  computed. */
    private Piece _winner;
    /** Number of (still undone) moves since initial position. */
    private int _moveCount;
    /** True when current board is a repeated position (ending the game). */
    private boolean _repeated;
    /** True when current board is a repeated position (ending the game). */
    private int _lim;
    /** move limit. */
    private Piece[][] _board;
    /** 2d array of pieces. */
    private ArrayList<String> _boardStates;
    /** list of encoded boards. */
    private Stack<ArrayList<Action>> _actionStack;

}
