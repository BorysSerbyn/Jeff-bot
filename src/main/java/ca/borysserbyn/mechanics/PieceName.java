package ca.borysserbyn.mechanics;

import java.util.ArrayList;
import java.util.Locale;

public enum PieceName {
    PAWN,
    KNIGHT,
    BISHOP,
    ROOK,
    QUEEN,
    KING,
    UNDEFINED;

    private static final float[][] pawnPositionValue = new float[][]{
            {1.00f,1.00f,1.00f,1.00f,1.00f,1.00f,1.00f,1.00f},
            {1.00f,1.00f,1.00f,1.00f,1.00f,1.00f,1.00f,1.00f},
            {1.00f,1.00f,1.00f,1.25f,1.25f,1.00f,1.00f,1.00f},
            {1.00f,1.00f,1.00f,1.50f,1.50f,1.00f,1.00f,1.00f},
            {1.00f,1.00f,1.00f,1.75f,1.75f,1.00f,1.00f,1.00f},
            {1.00f,1.00f,1.00f,1.75f,1.75f,1.00f,1.00f,1.00f},
            {1.00f,1.00f,1.00f,1.75f,1.75f,1.00f,1.00f,1.00f},
            {1.00f,1.00f,1.00f,1.00f,1.00f,1.00f,1.00f,1.00f}};
    private static final float[][] knightPositionValue = new float[][]{
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.25f,3.00f,3.00f,3.25f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.50f,3.50f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f}};
    private static final float[][] kingPositionValue = new float[][]{
            {0.10f,0.50f,0.15f,0.25f,0.10f,0.025f,0.3f,0.10f},
            {0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f},
            {0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f},
            {0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f},
            {0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f},
            {0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f},
            {0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f},
            {0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f}};
    private static final float[][] queenPositionValue = new float[][]{
            {9.00f,9.00f,9.00f,9.25f,9.25f,9.25f,9.00f,9.00f},
            {9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f},
            {9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f},
            {9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f},
            {9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f},
            {9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f},
            {9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f},
            {9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f}};
    private static final float[][] bishopPositionValue = new float[][]{
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f}};
    private static final float[][] rookPositionValue = new float[][]{
            {5.00f,5.00f,5.00f,5.00f,5.00f,5.00f,5.00f,5.00f},
            {5.00f,5.00f,5.00f,5.00f,5.00f,5.00f,5.00f,5.00f},
            {5.00f,5.00f,5.00f,5.00f,5.00f,5.00f,5.00f,5.00f},
            {5.00f,5.00f,5.00f,5.00f,5.00f,5.00f,5.00f,5.00f},
            {5.00f,5.00f,5.00f,5.00f,5.00f,5.00f,5.00f,5.00f},
            {5.00f,5.00f,5.00f,5.00f,5.00f,5.00f,5.00f,5.00f},
            {5.00f,5.00f,5.00f,5.00f,5.00f,5.00f,5.00f,5.00f},
            {5.00f,5.00f,5.00f,5.00f,5.00f,5.00f,5.00f,5.00f}};

    public float[][] getValueArray(){
        switch (this) {
            case BISHOP:
                return bishopPositionValue;
            case KNIGHT:
                return knightPositionValue;
            case PAWN:
                return pawnPositionValue;
            case ROOK:
                return rookPositionValue;
            case KING:
                return kingPositionValue;
            case QUEEN:
                return queenPositionValue;
            default:
                return null;
        }
    }

    public String getSymbol(){
        switch (this) {
            case BISHOP:
                return "B";
            case KNIGHT:
                return "N";
            case PAWN:
                return "";
            case ROOK:
                return "R";
            case KING:
                return "K";
            case QUEEN:
                return "Q";
            default:
                return null;
        }
    }

    public static PieceName getPieceNameBySymbol(String symbol){
        symbol = symbol.toLowerCase(Locale.ROOT);
        switch (symbol) {
            case "b":
                return BISHOP;
            case "n":
                return KNIGHT;
            case "":
                return PAWN;
            case "r":
                return ROOK;
            case "k":
                return KING;
            case "q":
                return QUEEN;
            default:
                return null;
        }
    }
}
