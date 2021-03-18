package ca.borysserbyn.mechanics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class MoveGenUtilsTest {
    @Test
    void generateBishopMovesTest() {
        Piece testBishop = new Piece(Color.BLACK, PieceName.BISHOP,4, 3);
        String fenStr = "B7/8/8/8/4b3/8/8/7k b - - 0 1";
        int[][] expectedMoveArray = new int[][]{{3,4}, {2,5}, {1,6}, {0,7}, {5,2}, {6,1}};
        ArrayList<Move> expectedMovesList = new ArrayList<>();
        ArrayList<Move> moveList = testBishop.generateMoves(NotationUtils.createGameFromFen(fenStr));
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
        ArrayList<Move> moveList = testKnight.generateMoves(NotationUtils.createGameFromFen(fenStr));
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
        ArrayList<Move> moveList = testKing.generateMoves(NotationUtils.createGameFromFen(fenStr));
        for(int[] move : expectedMoveArray){
            expectedMovesList.add(new Move(testKing, move[0], move[1]));
        }

        Assertions.assertEquals(expectedMovesList, moveList);
    }

    @Test
    void generatePawnMovesTest1() {
        Piece testPawn = new Piece(Color.WHITE, PieceName.PAWN,3, 4);
        String fenStr = "7k/8/2p5/3Pp3/8/1p6/2P5/7K w - e6 0 1";
        int[][] expectedMoveArray = new int[][]{{3,5},{2,5},{4,5}};
        ArrayList<Move> expectedMovesList = new ArrayList<>();
        ArrayList<Move> moveList = testPawn.generateMoves(NotationUtils.createGameFromFen(fenStr));
        for(int[] move : expectedMoveArray){
            expectedMovesList.add(new Move(testPawn, move[0], move[1]));
        }

        Assertions.assertEquals(expectedMovesList, moveList);
    }

    @Test
    void generatePawnMovesTest2() {
        Piece testPawn = new Piece(Color.WHITE, PieceName.PAWN,2, 1);
        String fenStr = "7k/8/2p5/3Pp3/8/1p6/2P5/7K w - e6 0 1";
        int[][] expectedMoveArray = new int[][]{{2,2},{2,3},{1,2}};
        ArrayList<Move> expectedMovesList = new ArrayList<>();
        ArrayList<Move> moveList = testPawn.generateMoves(NotationUtils.createGameFromFen(fenStr));
        for(int[] move : expectedMoveArray){
            expectedMovesList.add(new Move(testPawn, move[0], move[1]));
        }

        Assertions.assertEquals(expectedMovesList, moveList);
    }

    @Test
    void generatePawnMovesTest3() {
        Piece testPawn = new Piece(Color.WHITE, PieceName.PAWN,1, 4);
        String fenStr = "8/8/8/KPp4r/8/7k/8/8 w - c6 0 1";
        int[][] expectedMoveArray = new int[][]{{1,5}};
        ArrayList<Move> expectedMovesList = new ArrayList<>();
        ArrayList<Move> moveList = testPawn.generateMoves(NotationUtils.createGameFromFen(fenStr));
        for(int[] move : expectedMoveArray){
            expectedMovesList.add(new Move(testPawn, move[0], move[1]));
        }

        Assertions.assertEquals(expectedMovesList, moveList);
    }

    @Test
    void generatePawnMovesTest4() {
        Piece testPawn = new Piece(Color.BLACK, PieceName.PAWN,2, 5);
        String fenStr = "rnbq1k1r/pp1Pbppp/2p5/3B4/8/8/PPP1NnPP/RNBQK2R b KQ - 1 8";
        int[][] expectedMoveArray = new int[][]{{2,4}, {3,4}};
        ArrayList<Move> expectedMovesList = new ArrayList<>();
        ArrayList<Move> moveList = testPawn.generateMoves(NotationUtils.createGameFromFen(fenStr));
        for(int[] move : expectedMoveArray){
            expectedMovesList.add(new Move(testPawn, move[0], move[1]));
        }

        Assertions.assertEquals(expectedMovesList, moveList);
    }

    @Test
    void generateCastlingMovesTest() {
        Piece testKing = new Piece(Color.WHITE, PieceName.KING,4, 0);
        String fenStr = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/R3K2R w KQkq - 0 1";
        int[][] expectedMoveArray = new int[][]{{3,0},{5,0},{6,0},{2,0}};
        ArrayList<Move> expectedMovesList = new ArrayList<>();
        ArrayList<Move> moveList = testKing.generateMoves(NotationUtils.createGameFromFen(fenStr));

        for(int[] move : expectedMoveArray){
            expectedMovesList.add(new Move(testKing, move[0], move[1]));
        }
        Assertions.assertEquals(expectedMovesList, moveList);
    }

    @Test
    void generatePawnMovesTestGaming() {
        Piece testPawn = new Piece(Color.WHITE, PieceName.PAWN,3, 1);
        String fenStr = "rnb1kbnr/pp1ppppp/8/q1p5/8/P7/RPPPPPPP/1NBQKBNR w Kkq - 0 1";
        int[][] expectedMoveArray = new int[][]{};
        ArrayList<Move> expectedMovesList = new ArrayList<>();
        ArrayList<Move> moveList = testPawn.generateMoves(NotationUtils.createGameFromFen(fenStr));

        for(int[] move : expectedMoveArray){
            expectedMovesList.add(new Move(testPawn, move[0], move[1]));
        }

        Assertions.assertEquals(expectedMovesList, moveList);
    }

    @Test
    void generateKingMovesTest2() {
        Piece testKing = new Piece(Color.WHITE, PieceName.KING,4,0);
        String fenStr = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        int[][] expectedMoveArray = new int[][]{};
        ArrayList<Move> expectedMovesList = new ArrayList<>();
        ArrayList<Move> moveList = testKing.generateMoves(NotationUtils.createGameFromFen(fenStr));
        for(int[] move : expectedMoveArray){
            expectedMovesList.add(new Move(testKing, move[0], move[1]));
        }

        Assertions.assertEquals(expectedMovesList, moveList);
    }
}
