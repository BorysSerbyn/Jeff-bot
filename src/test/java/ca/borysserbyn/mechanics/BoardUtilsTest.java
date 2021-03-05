package ca.borysserbyn.mechanics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BoardUtilsTest {

    @Test
    void loadBoardFromFenTest(){

        Piece testBishop = new Piece(Color.BLACK, PieceName.BISHOP,3, 3);
        Piece[][] expectedBoard = new Piece[8][8];
        expectedBoard[3][3] = testBishop;
        String fenStr = "8/8/8/8/3b/8/8/8";
        Piece[][] obtainedBoard = BoardUtils.loadBoardFromFen(fenStr);
        Assertions.assertArrayEquals(expectedBoard, obtainedBoard);

    }

    @Test
    void fenFromBoard(){
        String expectedFenStr = "rnbqkbnr/ppppp1p1/5p2/7p/3P4/4P3/PPP2PPP/RNBQKBNR";
        Piece[][] boardToTranslate = BoardUtils.loadBoardFromFen(expectedFenStr);
        String obtainedFenStr = BoardUtils.createFenFromBoard(boardToTranslate);
        Assertions.assertEquals(expectedFenStr, obtainedFenStr);
    }
}
