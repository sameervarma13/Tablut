package tablut;

/** An action in a game of Tablut.
 *  @author Sameer Varma
 */
public class Action {
    /** Piece. */
    private Piece _piece;
    /** Square. */
    private Square _square;

    /** Creates action object. S. P. */
    public Action(Square s, Piece p) {
        _piece = p;
        _square = s;
    }
    /** Returns action piece. */
    public Piece getPiece() {
        return _piece;
    }
    /** Returns action square. */
    public Square getSquare() {
        return _square;
    }
}
