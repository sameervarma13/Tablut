package tablut;

import org.junit.Test;
import static org.junit.Assert.*;
import ucb.junit.textui;

/** The suite of all JUnit tests for the enigma package.
 *  @author
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** A dummy test as a placeholder for real ones. */
    @Test
    public void dummyTest() {
        Board b = new Board();
        b.makeMove(Move.mv("h5-6"));
        b.makeMove(Move.mv("g5-2"));
        b.makeMove(Move.mv("e8-c"));
        b.makeMove(Move.mv("e4-h"));

    }

}


