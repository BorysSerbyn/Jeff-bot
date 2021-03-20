package ca.borysserbyn.mechanics;

import ca.borysserbyn.jeffbot.Jeffbot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JeffTest {
    @Test
    public void willJeffCastle(){
        String fenStr = "4k2r/8/8/8/8/8/8/4K3 b k - 0 1";
        Game game = NotationUtils.createGameFromFen(fenStr);
        Jeffbot jeff = new Jeffbot(Color.BLACK, game, 4);
        jeff.setGame(game);
        Move actualMove = jeff.findBestMove();
        Move expectedMove = new Move(new Piece(Color.BLACK, PieceName.KING, 4,7), 6,7);

        Assertions.assertEquals(expectedMove, actualMove);
    }

    @Test
    public void willJeffPromote(){
        String fenStr = "k7/6P1/8/8/8/8/8/7K w - - 0 1";
        Game game = NotationUtils.createGameFromFen(fenStr);
        Jeffbot jeff = new Jeffbot(Color.WHITE, game, 1);
        jeff.setGame(game);
        Move actualMove = jeff.findBestMove();
        Move expectedMove = new Move(new Piece(Color.WHITE, PieceName.PAWN, 6,6), 6,7);

        Assertions.assertEquals(expectedMove, actualMove);
    }

    @Test
    void findBestMoveTest(){
        String fenStr = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Game game = NotationUtils.createGameFromFen(fenStr);
        Jeffbot expectedJeff = new Jeffbot(Color.WHITE, game, 5);
        Jeffbot actualJeff = new Jeffbot(Color.WHITE, game, 5);
        expectedJeff.resetCurrentNode();
        actualJeff.resetCurrentNode();
        expectedJeff.getCurrentNode().addNodes(0, (Game) game.clone(), false);
        actualJeff.getCurrentNode().testMinimax(0, (Game) game.clone(), -10000, 10000);
        Move expectedMove = expectedJeff.findBestMove();
        System.out.println();
        Move actualMove = actualJeff.findBestMove();

        Assertions.assertEquals(expectedMove, actualMove);

    }
}
