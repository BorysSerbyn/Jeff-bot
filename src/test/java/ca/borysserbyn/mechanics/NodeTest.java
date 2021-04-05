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
            //String expString = FileUtils.readTreeFile();
            String expString = "";
            Game game = NotationUtils.createGameFromFen("rnbqkbnr/pppp1ppp/8/3Np3/8/8/PPPPPPPP/R1BQKBNR b KQkq - 0 1");
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
            Assertions.assertArrayEquals(expList.toArray(), actList.toArray());

        }catch(Exception e){

        }
    }

    @Test
    void addNodesTest2(){
        //GameGUI.createJFrame(TestPanel.getSingletonInstance());
        try{
            //String expString = FileUtils.readTreeFile();
            String expString = "a2a3: 64258\n" +
                    "b2b3: 63791\n" +
                    "c2c3: 66476\n" +
                    "g2g3: 60312\n" +
                    "h2h3: 64282\n" +
                    "d3d4: 62871\n" +
                    "a2a4: 67743\n" +
                    "b2b4: 64402\n" +
                    "g2g4: 59611\n" +
                    "h2h4: 66015\n" +
                    "b1d2: 58975\n" +
                    "b1a3: 60259\n" +
                    "b1c3: 67983\n" +
                    "e2g1: 63874\n" +
                    "e2c3: 71375\n" +
                    "e2g3: 69004\n" +
                    "e2d4: 69458\n" +
                    "e2f4: 66042\n" +
                    "c1d2: 69612\n" +
                    "c1e3: 75748\n" +
                    "c1f4: 73727\n" +
                    "c1g5: 65457\n" +
                    "c1h6: 59313\n" +
                    "c4b3: 60554\n" +
                    "c4b5: 62909\n" +
                    "c4d5: 64073\n" +
                    "c4a6: 58467\n" +
                    "c4e6: 64081\n" +
                    "c4f7: 58378\n" +
                    "h1f1: 62889\n" +
                    "h1g1: 60616\n" +
                    "d1d2: 66093\n" +
                    "e1f1: 65925\n" +
                    "e1d2: 53967\n" +
                    "e1f2: 54653\n" +
                    "e1g1: 62283";
            Game game = NotationUtils.createGameFromFen("rnbq1k1r/pp2bppp/2p5/8/2B5/3P4/PPP1NnPP/RNBQK2R w KQ - 1 8");
            Node node = new Node(4, Color.WHITE, null, 0);
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
            Assertions.assertArrayEquals(expList.toArray(), actList.toArray());

        }catch(Exception e){

        }
    }

}
