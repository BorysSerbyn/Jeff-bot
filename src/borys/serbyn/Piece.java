package borys.serbyn;

import java.awt.*;
import java.util.Objects;

public class Piece implements Cloneable{
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
        return Objects.equals(color, piece.color) &&
                pieceName == piece.pieceName &&
                Objects.equals(tile, piece.tile);
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

}
