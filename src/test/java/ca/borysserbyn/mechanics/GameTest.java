package ca.borysserbyn.mechanics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class GameTest {
    void generateLegalMovesByColorTest(){
        String fenStr = "3k4/3p4/8/8/8/8/3P4/3K4 w - - 0 1";
        ArrayList<Move> expectedMovesList = new ArrayList<>();
        Game game = FenUtils.createGameFromFen(fenStr);
        ArrayList<Move> moveList = game.generateLegalMovesByColor(Color.WHITE);
        Assertions.assertEquals(expectedMovesList, moveList);
    }
}
