package ca.borysserbyn.mechanics;

import java.util.ArrayList;

public class BitBoard {


    public long[] bitBoardArray = new long[14];

    public BitBoard() {
        zeroOutBitBoard();
    }

    public void zeroOutBitBoard(){
        for (int i = 0; i < 14; i++) {
            bitBoardArray[i] = 0;
        }
    }


    public static void initializeBitBoardArray(ArrayList<Piece> pieces, long[] bitBoardArray){
        for (int i = 0; i < 14; i++) {
            bitBoardArray[i] = 0;
        }
        for (Piece piece : pieces) {
            if(piece.getX() == -1){
                continue;
            }
            turnOnBitByPiece(piece, bitBoardArray);
        }
    }

    public static boolean isPieceInArray(int x, int y, long[] bitBoardArray){
        for (int i = 0; i < 12; i++) {
            if(isPieceInBitBoard(x, y, bitBoardArray[i])){
                return true;
            }
        }
        return false;
    }

    public static boolean isPieceInBitBoard(int x, int y, long bitBoard){
        return bitBoard == (bitBoard & (1 << (y * 8 + x)));
    }

    public static long[] cloneArray(long[] bitBoardArray){
        long[] newArray = new long[14];
        for (int i = 0; i < 14; i++) {
            newArray[i] = bitBoardArray[i];
        }
        return newArray;
    }

    public static void turnOnBitByPiece(Piece piece, long[] bitBoardArray) {
        int index = getIndexByPiece(piece.getPieceName(), piece.getColor());
        bitBoardArray[index] = bitBoardArray[index] ^ (1 << (piece.getY() * 8 + piece.getX()));
    }

    public static void turnOffBitByPiece(Piece piece, long[] bitBoardArray) {
        int index = getIndexByPiece(piece.getPieceName(), piece.getColor());
        bitBoardArray[index] = bitBoardArray[index] & ~(1 << (piece.getY() * 8 + piece.getX()));
    }

    public static void updateWhitePieces(long[] bitBoardArray){
        bitBoardArray[12] = bitBoardArray[0] & bitBoardArray[1] & bitBoardArray[2] & bitBoardArray[3] & bitBoardArray[4] & bitBoardArray[5];
    }

    public static void updateBlackPieces(long[] bitBoardArray){
        bitBoardArray[13] = bitBoardArray[6] & bitBoardArray[7] & bitBoardArray[8] & bitBoardArray[9] & bitBoardArray[10] & bitBoardArray[11];
    }

    public long[] getBitBoardArray() {
        return bitBoardArray;
    }

    public static int getIndexByPiece(PieceName pieceName, Color color) {
        if (color == Color.WHITE) {
            if (pieceName == PieceName.PAWN) {
                return 0;
            }
            if (pieceName == PieceName.KING) {
                return 1;
            }
            if (pieceName == PieceName.KNIGHT) {
                return 2;
            }
            if (pieceName == PieceName.BISHOP) {
                return 3;
            }
            if (pieceName == PieceName.ROOK) {
                return 4;
            }
            if (pieceName == PieceName.QUEEN) {
                return 5;
            }
        } else {
            if (pieceName == PieceName.PAWN) {
                return 6;
            }
            if (pieceName == PieceName.KING) {
                return 7;
            }
            if (pieceName == PieceName.KNIGHT) {
                return 8;
            }
            if (pieceName == PieceName.BISHOP) {
                return 9;
            }
            if (pieceName == PieceName.ROOK) {
                return 10;
            }
            if (pieceName == PieceName.QUEEN) {
                return 11;
            }
        }
        return 0;
    }
}
