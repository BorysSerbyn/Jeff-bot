package ca.borysserbyn.mechanics;

import ca.borysserbyn.jeffbot.Jeffbot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JeffTest {
    @Test
    public void willJeffCastle(){
        String fenStr = "4k2r/8/8/8/8/8/8/4K3 b k - 0 1";
        Game game = NotationUtils.createGameFromFen(fenStr);
        Jeffbot jeff = new Jeffbot(Color.BLACK, game, 2);
        jeff.setGame(game);
        Move actualMove = jeff.findBestMove();
        Move expectedMove = new Move(new Piece(Color.BLACK, PieceName.KING, 4,7), 6,7);

        Assertions.assertEquals(expectedMove, actualMove);
    }
}
