package ca.borysserbyn.mechanics;

import java.io.Serializable;

public class Move implements Cloneable, Serializable {
    private Piece piece;
    private int x;
    private int y;


    public Move(Piece piece, int x, int y) {
        this.piece = piece;
        this.x = x;
        this.y = y;
    }
    @Override
    public Object clone() {
        Move move = null;
        try {
            move = (Move) super.clone();
        } catch (CloneNotSupportedException e) {
            move = new Move(piece, x, y);
        }
        move.piece = (Piece) piece.clone();
        return move;
    }

    public String toSFNotation(){
        char pieceLetter = (char) (piece.getX() + 97);
        char targetLetter = (char) (x + 97);
        return "" + pieceLetter + (piece.getY() + 1) + targetLetter + (y+1);
    }

    @Override
    public String toString() {
        return piece.toString() + " TO--> " + "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return this.piece.equals(move.piece) &&
                x == move.x &&
                y == move.y;
    }

    public Piece moveToPiece(){
        Piece clonedPiece = (Piece) piece.clone();
        clonedPiece.setTile(x, y);
        return clonedPiece;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }
    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void setTile(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
