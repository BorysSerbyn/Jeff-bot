package ca.borysserbyn.mechanics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FenUtilsTest {

    @Test
    void createGameFromFen(){
        String expectedFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Game actualGame = FenUtils.createGameFromFen(expectedFen);
        Game expectedGame = new Game(1);
        String actualFen = FenUtils.createFenFromGame(actualGame);
        Assertions.assertEquals(expectedFen, actualFen);
    }

    @Test
    void createFenFromGame(){
        String expectedFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Game game = new Game(1);
        String actualFen = FenUtils.createFenFromGame(game);
        Assertions.assertEquals(expectedFen, actualFen);
    }

    @Test
    void createBoardFromFenTest(){
        Piece testBishop = new Piece(Color.BLACK, PieceName.BISHOP,3, 3);
        Piece[][] expectedBoard = new Piece[8][8];
        expectedBoard[3][3] = testBishop;
        String fenStr = "8/8/8/8/3b/8/8/8";
        Piece[][] actualBoard = FenUtils.createBoardFromFen(fenStr);
        Assertions.assertArrayEquals(expectedBoard, actualBoard);
    }

    @Test
    void fenFromBoard(){
        String expectedFen = "rnbqkbnr/ppppp1p1/5p2/7p/3P4/4P3/PPP2PPP/RNBQKBNR";
        Piece[][] boardToTranslate = FenUtils.createBoardFromFen(expectedFen);
        String actualFen = FenUtils.createFenFromBoard(boardToTranslate);
        Assertions.assertEquals(expectedFen, actualFen);
    }
}
