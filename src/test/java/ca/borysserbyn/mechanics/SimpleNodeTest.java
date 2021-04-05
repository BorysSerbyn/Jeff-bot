package ca.borysserbyn.mechanics;

import ca.borysserbyn.jeffbot.SimpleNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.*;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;

public class SimpleNodeTest {

    @Test
    void addNodesTest(){
        //GameGUI.createJFrame(TestPanel.getSingletonInstance());
        try{
            String expString = FileUtils.readTreeFile();
            Game game = NotationUtils.createGameFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            SimpleNode node = new SimpleNode(5, Color.WHITE, null);
            String actString = tapSystemOut(() -> {
                node.addNodes(0, game);
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

    @Test
    void addNodesTest4(){
        Game game = NotationUtils.createGameFromFen("rnbg1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
        SimpleNode node = new SimpleNode(1, Color.WHITE, null);
        int[] actualMoves = new int[5];
        int[] expectedMoves = new int[]{44, 1486, 62379, 2103487, 89941194};
        actualMoves[0] = node.addNodes(1, game);
        actualMoves[1] = node.addNodes(2, game);
        actualMoves[2] = node.addNodes(3, game);
        actualMoves[3] = node.addNodes(4, game);
        actualMoves[4] = node.addNodes(5, game);
        Assertions.assertArrayEquals(expectedMoves, actualMoves);
    }
}
