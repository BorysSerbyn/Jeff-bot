package ca.borysserbyn.mechanics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class PieceTest {
    @Test
    void generateBishopMovesTest() {
        Piece testBishop = new Piece(Color.BLACK, PieceName.BISHOP,3, 3);
        String fenStr = "8/8/5r2/8/3b4/8/8/8";
        int[][] expectedMoveArray = new int[][]{{4, 4}, {2, 2}, {1, 1}, {0, 0}, {2, 4}, {1, 5}, {0, 6}, {4, 2}, {5, 1}, {6, 0}};
        ArrayList<Move> expectedMovesList = new ArrayList<>();
        ArrayList<Move> moveList = testBishop.generateMoves(BoardUtils.loadBoardFromFen(fenStr));
        for(int[] move : expectedMoveArray){
            expectedMovesList.add(new Move(testBishop, move[0], move[1]));
        }

        Assertions.assertEquals(expectedMovesList, moveList);
    }

    void generateRookMovesTest() {

    }
}
