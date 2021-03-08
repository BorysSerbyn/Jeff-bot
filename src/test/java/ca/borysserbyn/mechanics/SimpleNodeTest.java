package ca.borysserbyn.mechanics;

import ca.borysserbyn.gui.GameGUI;
import ca.borysserbyn.gui.TestPanel;
import ca.borysserbyn.jeffbot.SimpleNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleNodeTest {

    @Test
    void addNodesTest(){

        GameGUI.createJFrame(TestPanel.getSingletonInstance());
        Game game = new Game(1);
        SimpleNode node = new SimpleNode(2, Color.WHITE, null);
        String actualMoves = node.addNodesMoves(0, game);
        String expectedMoves = FileUtils.readTreeFile();
        Assertions.assertEquals(expectedMoves, actualMoves);
    }

    @Test
    void addNodesTest2(){
        Game game = FenUtils.createGameFromFen("rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQKB1R b KQkq - 0 1");
        SimpleNode node = new SimpleNode(1, Color.WHITE, null);
        String actualMoves = node.addNodesMoves(0, game);
        String expectedMoves = "test";
        Assertions.assertEquals(expectedMoves, actualMoves);
    }

    @Test
    void addNodesTest3(){
        Game game = FenUtils.createGameFromFen("rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq - 0 1");
        SimpleNode node = new SimpleNode(1, Color.WHITE, null);
        String actualMoves = node.addNodesMoves(0, game);
        String expectedMoves = "test";
        Assertions.assertEquals(expectedMoves, actualMoves);
    }
}
