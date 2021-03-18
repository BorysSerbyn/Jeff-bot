package ca.borysserbyn.mechanics;

import ca.borysserbyn.jeffbot.Jeffbot;
import ca.borysserbyn.jeffbot.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NodeTest {
    @Test
    public void compareTest(){
        Node node = new Node(5, Color.WHITE, new Move(new Piece(Color.BLACK, PieceName.PAWN, 1, 2), 2, 3), 3);
        node.setCascadedScore(0.1f);
        Node otherNode = new Node(5, Color.WHITE, new Move(new Piece(Color.BLACK, PieceName.PAWN, 1, 2), 2, 3), 3);
        otherNode.setCascadedScore(-0.1f);
        int expected = -1;
        int actual = node.compareTo(otherNode);
        Assertions.assertEquals(expected, actual);
    }


}
