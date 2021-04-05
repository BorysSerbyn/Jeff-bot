package ca.borysserbyn.mechanics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

public class MoveTest {

    @Test
    void orderMovesTest(){
        String fenStr = "k7/8/1q3b2/8/3B4/8/5p2/7K w - - 0 1";
        Game game = NotationUtils.createGameFromFen(fenStr);
        MoveComparator moveComparator = new MoveComparator(game);
        ArrayList<Move> actualMoves = new ArrayList<>();
        ArrayList<Move> expectedMoves = new ArrayList<>();
        Piece whiteBishop = new Piece(Color.WHITE, PieceName.BISHOP, 3,3);
        //System.out.println(game.getPieceByTile(1,5));
        actualMoves.add(new Move(whiteBishop, 5,1));
        actualMoves.add(new Move(whiteBishop, 5,5));
        actualMoves.add(new Move(whiteBishop, 1,1));
        actualMoves.add(new Move(whiteBishop, 1,5));
        expectedMoves.add(new Move(whiteBishop, 1,5));
        expectedMoves.add(new Move(whiteBishop, 5,5));
        expectedMoves.add(new Move(whiteBishop, 5,1));
        expectedMoves.add(new Move(whiteBishop, 1,1));
        actualMoves.sort(moveComparator);
        //Collections.sort(actualMoves, moveComparator);
        Assertions.assertEquals(expectedMoves, actualMoves);
    }
}
