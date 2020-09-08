package tablut;

import org.junit.Test;
import static org.junit.Assert.*;
import ucb.junit.textui;
import java.util.List;

/** Junit tests for our Tablut Board class.
 *  @author Vivant Sakore
 */
public class TablutTests {

    /** Run the JUnit tests in this package. */
    public static void main(String[] ignored) {
        textui.runClasses(TablutTests.class);
    }

    /** Test. */
    @Test
    public void testLegalWhiteMoves() {
        Board b = new Board();
        b.makeMove(Move.mv("a4-3"));
        b.makeMove(Move.mv("e4-f"));
        b.makeMove(Move.mv("b5-3"));
        b.makeMove(Move.mv("e5-4"));
        b.makeMove(Move.mv("b3-5"));

        List<Move> movesList = b.legalMoves(B);
        for (Move m : movesList) {
            System.out.println(m);
        }

        assertFalse(movesList.contains(Move.mv("d4-3")));
        assertFalse(movesList.contains(Move.mv("b5-4")));

        assertTrue(movesList.contains(Move.mv("d3-2")));
        assertTrue(movesList.contains(Move.mv("d3-4")));

    }

    /** Test. */
    @Test
    public void testLegalBlackMoves() {
        Board b = new Board();

        List<Move> movesList = b.legalMoves(B);
        assertEquals(80, movesList.size());

        assertFalse(movesList.contains(Move.mv("e8-7")));
        assertFalse(movesList.contains(Move.mv("e7-8")));

        assertTrue(movesList.contains(Move.mv("f9-i")));
        assertTrue(movesList.contains(Move.mv("h5-1")));

    }



    private void buildBoard(Board b, Piece[][] target) {
        for (int col = 0; col < Board.SIZE; col++) {
            for (int row = Board.SIZE - 1; row >= 0; row--) {
                Piece piece = target[Board.SIZE - 1 - row][col];
                b.put(piece, Square.sq(col, row));
            }
        }
        System.out.println(b);
    }

    static final Piece E = Piece.EMPTY;
    static final Piece W = Piece.WHITE;
    static final Piece B = Piece.BLACK;
    static final Piece K = Piece.KING;

    static  Piece[][] initialBoardState = {
            {E, E, E, B, B, B, E, E, E},
            {E, E, E, E, B, E, E, E, E},
            {E, E, E, E, W, E, E, E, E},
            {B, E, E, E, W, E, E, E, B},
            {B, B, W, W, K, W, W, B, B},
            {B, E, E, E, W, E, E, E, B},
            {E, E, E, E, W, E, E, E, E},
            {E, E, E, E, B, E, E, E, E},
            {E, E, E, B, B, B, E, E, E},
    };
}
