package ca.borysserbyn;

import java.util.Objects;

public class Move implements Cloneable{
    private Piece piece;
    private Tile tile;

    public Move(Piece piece, Tile tile) {
        this.piece = piece;
        this.tile = tile;
    }
    @Override
    public Object clone() {
        Move move = null;
        try {
            move = (Move) super.clone();
        } catch (CloneNotSupportedException e) {
            move = new Move(piece, tile);
        }
        move.piece = (Piece) piece.clone();
        move.tile = (Tile) tile.clone();
        return move;
    }

    @Override
    public String toString() {
        return piece.toString() + " to: " + tile.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return this.piece.equals(move.piece) &&
                this.tile.equals(move.tile);
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }
}
