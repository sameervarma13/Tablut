package tablut;



/** A Player that automatically generates moves.
 *  @author Sameer Varma
 */
class AI extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A position-score magnitude indicating a forced win in a subsequent
     *  move.  This differs from WINNING_VALUE to avoid putting off wins. */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;
    /** Double value used to cause small randomization of moves. */
    private static final double RANDOM = 0.001;


    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        int s;
        if (myPiece() == Piece.WHITE) {
            s = 1;
        } else {
            s = -1;
        }
        Board b = new Board(board());
        findMove(b, maxDepth(b), true, s, -INFTY, INFTY);
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;
    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }
        int val;
        if (sense == 1) {
            val = -INFTY;
            for (Move m: board.legalMoves(Piece.WHITE)) {
                Board board2 = new Board(board);
                if (board2.isLegal(m)) {
                    board2.makeMove(m);
                } else {
                    continue;
                }
                int response = findMove(board2, depth - 1,
                        false, -sense, alpha, beta);
                if (response >= val) {
                    if (saveMove) {
                        _lastFoundMove = m;
                    }
                    val = response;
                    alpha = Integer.max(alpha, val);
                    if (val >= beta && Math.random() < RANDOM) {
                        break;
                    }
                }
            }
        } else {
            val = INFTY;
            for (Move m: board.legalMoves(Piece.BLACK)) {
                Board board2 = new Board(board);
                if (board2.isLegal(m)) {
                    board2.makeMove(m);
                } else {
                    continue;
                }
                int response = findMove(board2, depth - 1,
                        false, -sense, alpha, beta);
                if (response <= val) {
                    val = response;
                    if (saveMove) {
                        _lastFoundMove = m;
                    }
                    beta = Integer.min(beta, val);
                    if (val <= alpha || Math.random() < RANDOM) {
                        break;
                    }
                }
            }
        }
        return val;
    }
    /** Max Depth. Return. BOARD.*/
    private static int maxDepth(Board board) {
        return 1;
    }
    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        Piece winner = board.winner();
        if (winner == Piece.BLACK) {
            return -WINNING_VALUE;
        } else if (winner == Piece.WHITE) {
            return WINNING_VALUE;
        }

        int wCount = 0;
        int bCount = 0;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board.get(i, j) == Piece.BLACK) {
                    bCount++;
                } else if (board.get(i, j) == Piece.WHITE) {
                    wCount++;
                }
            }
        }
        return wCount - bCount;
    }

}
