package ca.borysserbyn.mechanics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BitBoardTest {
    @Test
    void flipTestPawn(){
        BitBoard bitBoard = new BitBoard();
        Piece blackPawn = new Piece(Color.BLACK, PieceName.PAWN, 1, 0);
        bitBoard.turnOnBitByPiece(blackPawn, bitBoard.getBitBoardArray());
        int index = bitBoard.getIndexByPiece(blackPawn.getPieceName(), blackPawn.getColor());
        long actualBitBoard = bitBoard.getBitBoardArray()[index];
        Assertions.assertEquals(2, actualBitBoard);
    }

    @Test
    void flipTestPawn5(){
        BitBoard bitBoard = new BitBoard();
        Piece blackPawn = new Piece(Color.BLACK, PieceName.PAWN, 7, 7);
        bitBoard.turnOnBitByPiece(blackPawn, bitBoard.getBitBoardArray());
        int index = bitBoard.getIndexByPiece(blackPawn.getPieceName(), blackPawn.getColor());
        long actualBitBoard = bitBoard.getBitBoardArray()[index];
        Assertions.assertEquals(2, actualBitBoard);
    }

    @Test
    void flipTestPawn2(){
        BitBoard bitBoard = new BitBoard();
        Piece whitePawn = new Piece(Color.WHITE, PieceName.PAWN, 2, 0);
        bitBoard.turnOnBitByPiece(whitePawn, bitBoard.getBitBoardArray());
        int index = bitBoard.getIndexByPiece(whitePawn.getPieceName(), whitePawn.getColor());
        long actualBitBoard = bitBoard.getBitBoardArray()[index];
        Assertions.assertEquals(4, actualBitBoard);
    }

    @Test
    void flipTestPawn3(){
        BitBoard bitBoard = new BitBoard();
        Piece whitePawn = new Piece(Color.WHITE, PieceName.PAWN, 5, 0);
        Piece whitePawn2 = new Piece(Color.WHITE, PieceName.PAWN, 0, 1);
        bitBoard.turnOnBitByPiece(whitePawn, bitBoard.getBitBoardArray());
        bitBoard.turnOnBitByPiece(whitePawn2, bitBoard.getBitBoardArray());
        int index = bitBoard.getIndexByPiece(whitePawn.getPieceName(), whitePawn.getColor());
        long actualBitBoard = bitBoard.getBitBoardArray()[index];
        Assertions.assertEquals(0b100100000, actualBitBoard);
    }

    @Test
    void updateWhiteTest(){
        BitBoard bitBoard = new BitBoard();
        Piece whitePawn = new Piece(Color.WHITE, PieceName.PAWN, 5, 0);
        Piece whitePawn2 = new Piece(Color.WHITE, PieceName.PAWN, 0, 1);
        bitBoard.turnOnBitByPiece(whitePawn, bitBoard.getBitBoardArray());
        bitBoard.turnOnBitByPiece(whitePawn2, bitBoard.getBitBoardArray());
        BitBoard.updateBitBoard(bitBoard.getBitBoardArray());
        long actualBitBoard = bitBoard.getBitBoardArray()[12];
        Assertions.assertEquals(0b100100000, actualBitBoard);
    }

    @Test
    void isPieceThereTest(){
        BitBoard bitBoard = new BitBoard();
        Piece whitePawn = new Piece(Color.WHITE, PieceName.PAWN, 5, 0);
        Piece whitePawn2 = new Piece(Color.WHITE, PieceName.PAWN, 0, 1);
        bitBoard.turnOnBitByPiece(whitePawn, bitBoard.getBitBoardArray());
        bitBoard.turnOnBitByPiece(whitePawn2, bitBoard.getBitBoardArray());
        BitBoard.updateBitBoard(bitBoard.getBitBoardArray());


        long actualBitBoard = bitBoard.getBitBoardArray()[12];
        Assertions.assertEquals(0b100100000, actualBitBoard);
        Assertions.assertTrue(BitBoard.isPieceInBitBoard(5,0,bitBoard.getBitBoardArray()));
    }

    @Test
    void isPieceThereTest2(){
        BitBoard bitBoard = new BitBoard();
        Piece blackPawn = new Piece(Color.BLACK, PieceName.PAWN, 7, 7);
        Piece blackPawn2 = new Piece(Color.BLACK, PieceName.PAWN, 0, 7);
        bitBoard.turnOnBitByPiece(blackPawn, bitBoard.getBitBoardArray());
        bitBoard.turnOnBitByPiece(blackPawn2, bitBoard.getBitBoardArray());
        BitBoard.updateBitBoard(bitBoard.getBitBoardArray());
        Assertions.assertTrue(BitBoard.isColoredPieceInBitBoard(Color.BLACK, 7,7, bitBoard.getBitBoardArray()));
    }
}
