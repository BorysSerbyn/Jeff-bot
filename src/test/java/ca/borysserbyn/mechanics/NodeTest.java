package ca.borysserbyn.mechanics;

import ca.borysserbyn.jeffbot.Jeffbot;
import ca.borysserbyn.jeffbot.Node;
import ca.borysserbyn.jeffbot.SimpleNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;

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

    @Test
    void addNodesTest(){
        //GameGUI.createJFrame(TestPanel.getSingletonInstance());
        try{
            String expString = FileUtils.readTreeFile();
            Game game = NotationUtils.createGameFromFen("rnbqkbnr/pppp1ppp/8/4p3/6P1/5P2/PPPPP2P/RNBQKBNR b KQkq - 0 2");
            Node node = new Node(2, Color.WHITE, null, 3);
            String actString = tapSystemOut(() -> {
                node.addNodes(0, game, true);
            });

            String[] expArray = expString.split("\\r?\\n");
            String[] actArray = actString.split("\\r?\\n");
            List<String> actList = Arrays.asList(actArray);
            List<String> expList = Arrays.asList(expArray);

            Collections.sort(expList);
            Collections.sort(actList);

            expString = "";
            actString = "";

            for (String str:expList) {
                expString+=str;
                expString+="\n";
            }

            for (String str:actList) {
                actString+=str;
                actString+="\n";
            }

            Assertions.assertEquals(expString, actString);

            //Assertions.assertArrayEquals(expList.toArray(), actList.toArray());

        }catch(Exception e){

        }
    }

}
