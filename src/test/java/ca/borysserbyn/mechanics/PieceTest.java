package ca.borysserbyn.mechanics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class PieceTest {
    @Test
    void generateBishopMovesTest() {
        Piece testBishop = new Piece(Color.BLACK, PieceName.BISHOP,4, 3);
        String fenStr = "B7/8/8/8/4b3/8/8/7k b - - 0 1";
        int[][] expectedMoveArray = new int[][]{{3,4}, {2,5}, {1,6}, {0,7}, {5,2}, {6,1}};
        ArrayList<Move> expectedMovesList = new ArrayList<>();
        ArrayList<Move> moveList = testBishop.generateMoves(FenUtils.createGameFromFen(fenStr));
        for(int[] move : expectedMoveArray){
            expectedMovesList.add(new Move(testBishop, move[0], move[1]));
        }

        Assertions.assertEquals(expectedMovesList, moveList);
    }

    @Test
    void generateKnightMovesTest() {
        Piece testKnight = new Piece(Color.BLACK, PieceName.KNIGHT,4, 3);
        String fenStr = "3B4/8/8/8/4n2k/8/8/8 b - - 0 1";
        int[][] expectedMoveArray = new int[][]{{5,5}, {6,4}};
        ArrayList<Move> expectedMovesList = new ArrayList<>();
        ArrayList<Move> moveList = testKnight.generateMoves(FenUtils.createGameFromFen(fenStr));
        for(int[] move : expectedMoveArray){
            expectedMovesList.add(new Move(testKnight, move[0], move[1]));
        }

        Assertions.assertEquals(expectedMovesList, moveList);
    }

    @Test
    void generateKingMovesTest() {
        Piece testKing = new Piece(Color.BLACK, PieceName.KING,7, 3);
        String fenStr = "3B4/8/8/8/4n1pk/6Np/8/8 b - - 0 1";
        int[][] expectedMoveArray = new int[][]{{6,2}};
        ArrayList<Move> expectedMovesList = new ArrayList<>();
        ArrayList<Move> moveList = testKing.generateMoves(FenUtils.createGameFromFen(fenStr));
        for(int[] move : expectedMoveArray){
            expectedMovesList.add(new Move(testKing, move[0], move[1]));
        }

        Assertions.assertEquals(expectedMovesList, moveList);
    }
}
