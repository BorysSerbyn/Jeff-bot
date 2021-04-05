package ca.borysserbyn.mechanics;



import java.io.Serializable;
import java.util.ArrayList;

public class Piece implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;

    private Color color;
    private PieceName pieceName;
    private int x;
    private int y;


    public Piece(Color color, PieceName pieceName, int x, int y) {
        this.color = color;
        this.pieceName = pieceName;
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return color == piece.getColor() &&
                pieceName == piece.pieceName &&
                x == piece.x &&
                y == piece.y;
    }

    @Override
    public Object clone() {
        try {
            return (Piece) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Piece(this.getColor(), this.getPieceName(), this.x, this.y);
        }
    }

    @Override
    public String toString() {
        String pieceString = new String();
        pieceString += color == Color.WHITE ? "WHITE_" : "BLACK_";
        pieceString += pieceName.name() + " ";
        pieceString += "(" + x + ", " + y + ")";
        return pieceString;
    }

    public void setPieceName(PieceName pieceName) {
        this.pieceName = pieceName;
    }

    public void setTile(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public PieceName getPieceName() {
        return pieceName;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void discardPiece(){
        x = -1;
        y = -1;
    }

    public float getValue(int orientation){
        int x = 0;
        int y = 0;
        if(orientation == 1){
            //flip it verticaly if its black
            x = color == Color.WHITE ? this.x :this.x;
            y = color == Color.WHITE ? this.y : 7-this.y;
        }else{
            //rotate if white
            //flip horizontaly if black
            x = color == Color.WHITE ? 7-this.x : this.x;
            y = color == Color.WHITE ? 7-this.y : this.y;
        }
        return pieceName.getValueArray()[y][x];
    }

    public ArrayList generateMoves(Game game){
        switch (pieceName) {
            case BISHOP:
                return MoveGenUtils.generateBiShopMoves(game, this);
            case KNIGHT:
                return MoveGenUtils.generateKnightMoves(game, this);
            case PAWN:
                return MoveGenUtils.generatePawnMoves(game, this);
            case ROOK:
                return MoveGenUtils.generateRookMoves(game, this);
            case KING:
                return MoveGenUtils.generateKingMoves(game, this);
            case QUEEN:
                return MoveGenUtils.generateQueenMoves(game, this);
            default:
                return null;
        }
    }


}
