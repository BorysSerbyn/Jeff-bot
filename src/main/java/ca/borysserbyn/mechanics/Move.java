package ca.borysserbyn.mechanics;

import java.io.Serializable;
import java.util.Locale;

public class Move implements Cloneable, Serializable {
    private Piece piece;
    private int x;
    private int y;
    private GameState stateSnapShot = GameState.UNDEFINED;
    private PieceName promotionSnapShot = PieceName.UNDEFINED;


    public Move(Piece piece, int x, int y) {
        this.piece = piece;
        this.x = x;
        this.y = y;
    }

    public Move(Piece piece, int x, int y, GameState stateSnapShot, PieceName promotionSnapShot) {
        this.piece = piece;
        this.x = x;
        this.y = y;
        this.stateSnapShot = stateSnapShot;
        this.promotionSnapShot = promotionSnapShot;
    }

    @Override
    public Object clone() {
        return new Move((Piece) piece.clone(), x, y, stateSnapShot, promotionSnapShot);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public PieceName getPromotionSnapShot() {
        return promotionSnapShot;
    }

    public void setPromotionSnapShot(PieceName promotionSnapShot) {
        this.promotionSnapShot = promotionSnapShot;
    }

    public GameState getStateSnapShot() {
        return stateSnapShot;
    }

    public void setStateSnapShot(GameState stateSnapShot) {
        this.stateSnapShot = stateSnapShot;
    }

    public String toUciNotation(){
        char pieceLetter = (char) (piece.getX() + 97);
        char targetLetter = (char) (x + 97);
        String uciStr = "" + pieceLetter + (piece.getY() + 1) + targetLetter + (y+1);
        if(promotionSnapShot != PieceName.UNDEFINED){
            uciStr += promotionSnapShot.getSymbol().toLowerCase(Locale.ROOT);
        }
        return uciStr;
    }

    public String toPGNNotation() {
        String pgnStr = "";
        String pieceName = piece.getPieceName().getSymbol();
        char targetLetter = (char) (x + 97);

        if(promotionSnapShot != PieceName.UNDEFINED){
            pieceName += "=" + promotionSnapShot.getSymbol();
        }

        if (stateSnapShot == GameState.CASTLING_SHORT){
            pgnStr = "O-O";
        }else if(stateSnapShot == GameState.CASTLING_LONG){
            pgnStr = "O-O-O";
        }

        if(stateSnapShot == GameState.PIECE_EATEN){
            pgnStr += pieceName;
            pgnStr += targetLetter;
            pgnStr += y+1;
        }else if(stateSnapShot == GameState.CHECKMATE){
            pgnStr += pieceName;
            pgnStr += targetLetter;
            pgnStr += y+1;
            pgnStr += "#";
        }else if(stateSnapShot == GameState.NEUTRAL){
            pgnStr += pieceName;
            pgnStr += targetLetter;
            pgnStr += y+1;
        }

        return pgnStr;
    }

    @Override
    public String toString() {
        String str = piece.toString() + " TO--> " + "(" + x + ", " + y + ")";
        str += promotionSnapShot != PieceName.UNDEFINED ? " " + promotionSnapShot : "";
        return str;
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
