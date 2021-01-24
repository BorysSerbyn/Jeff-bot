package ca.borysserbyn;


import ca.borysserbyn.Color;
import java.io.Serializable;
import java.util.Objects;

public class Piece implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;
    private static float[][] pawnPositionValue = new float[][]{
            {1.00f,1.00f,1.00f,1.00f,1.00f,1.00f,1.00f,1.00f},
            {1.00f,1.00f,1.00f,1.00f,1.00f,1.00f,1.00f,1.00f},
            {1.00f,1.00f,1.00f,1.25f,1.25f,1.00f,1.00f,1.00f},
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
            x = color == Color.WHITE ? this.x :this.x;
            y = color == Color.WHITE ? this.y : 7-this.y;
        }else{
            //rotate if white
            //flip horizontaly if black
            x = color == Color.WHITE ? 7-this.x : this.x;
            y = color == Color.WHITE ? 7-this.y : this.y;
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
