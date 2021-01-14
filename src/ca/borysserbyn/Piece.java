package ca.borysserbyn;

import java.awt.*;
import java.io.Serializable;
import java.util.Objects;

public class Piece implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;
    private static float[][] pawnPositionValue = new float[][]{
            {1.00f,1.00f,1.00f,1.00f,1.00f,1.00f,1.00f,1.00f},
            {1.00f,1.00f,1.00f,1.00f,1.00f,1.00f,1.00f,1.00f},
            {1.13f,1.00f,1.00f,1.25f,1.25f,1.00f,1.00f,1.13f},
            {1.00f,1.00f,1.00f,1.50f,1.50f,1.00f,1.00f,1.00f},
            {1.00f,1.00f,1.00f,1.75f,1.75f,1.00f,1.00f,1.00f},
            {1.00f,1.00f,1.00f,1.75f,1.75f,1.00f,1.00f,1.00f},
            {1.00f,1.00f,1.00f,1.75f,1.75f,1.00f,1.00f,1.00f},
            {1.00f,1.00f,1.00f,1.00f,1.00f,1.00f,1.00f,1.00f}};
    private static float[][] knightPositionValue = new float[][]{
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.25f,3.00f,3.00f,3.25f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.50f,3.50f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f},
            {3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f,3.00f}};
    private static float[][] kingPositionValue = new float[][]{
            {0.25f,0.50f,0.01f,0.25f,0.01f,0.025f,0.3f,0.25f},
            {0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f},
            {0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f},
            {0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f},
            {0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f},
            {0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f},
            {0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f},
            {0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f,0.00f}};
    private static float[][] queenPositionValue = new float[][]{
            {9.00f,9.00f,9.00f,9.25f,9.25f,9.25f,9.00f,9.00f},
            {9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f},
            {9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f},
            {9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f},
            {9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f},
            {9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f},
            {9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f},
            {9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f,9.00f}};


    private Color color;
    private PieceName pieceName;
    private Tile tile;

    public Piece(Color color, PieceName pieceName, Tile tile) {
        this.color = color;
        this.pieceName = pieceName;
        this.tile = tile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return color == piece.getColor() &&
                pieceName == piece.pieceName &&
                tile.equals(piece.getTile());
    }

    @Override
    public Object clone() {
        try {
            return (Piece) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Piece(this.getColor(), this.getPieceName(), this.getTile());
        }
    }

    @Override
    public String toString() {
        String pieceString = new String();
        pieceString += color == Color.WHITE ? "WHITE_" : "BLACK_";
        pieceString += pieceName.name();
        pieceString += " {" + tile.toString() + "}";
        return pieceString;
    }

    public void setPieceName(PieceName pieceName) {
        this.pieceName = pieceName;
    }

    public Color getColor() {
        return color;
    }

    public PieceName getPieceName() {
        return pieceName;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public void discardPiece(Tile tile){
        this.tile = tile;
    }

    public int getValue(){
        switch (pieceName) {
            case PAWN:
                return 1;
            case KNIGHT:
                return 3;
            case BISHOP:
                return 3;
            case ROOK:
                return 5;
            case KING:
                return 0;
            case QUEEN:
                return 9;
            default:
                return 0;
        }
    }

    public float getValue(int orientation){
        int x = 0;
        int y = 0;
        if(orientation == 1){
            //flip it verticaly if its black
            x = color == Color.WHITE ? tile.getX() : tile.getX();
            y = color == Color.WHITE ? tile.getY() : 7-tile.getY();
        }else{
            //rotate if white
            //flip horizontaly if black
            x = color == Color.WHITE ? 7-tile.getX() : 7-tile.getX();
            y = color == Color.WHITE ? 7-tile.getY() : tile.getY();
        }
        switch (pieceName) {
            case PAWN:
                return pawnPositionValue[y][x];
            case KNIGHT:
                return knightPositionValue[y][x];
            case BISHOP:
                return 3;
            case ROOK:
                return 5;
            case KING:
                return kingPositionValue[y][x];
            case QUEEN:
                return queenPositionValue[y][x];
            default:
                return 0;
        }
    }



}
