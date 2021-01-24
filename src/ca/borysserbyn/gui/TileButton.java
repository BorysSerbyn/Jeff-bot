package ca.borysserbyn.gui;

import ca.borysserbyn.Board;
import ca.borysserbyn.Color;
import ca.borysserbyn.Move;
import ca.borysserbyn.Piece;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class TileButton extends JButton {
    private Piece piece;
    private int x;
    private int y;

    public TileButton(int x, int y) {
        this.x = x;
        this.y = y;
        piece = null;
    }

    public void displayPiece(){
        System.out.println(piece.toString());
    }


    public void setTile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getXOnBoard(){
        return x;
    }

    public int getYOnBoard(){
        return y;
    }

    @Override
    public String toString() {
        return "Tile Button: (" + x + ", " + y + ")";
    }


    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        updateIcon();
    }

    public void setAndMovePiece(Board board, Piece piece) {
        board.movePiece(new Move(piece, x, y));
        this.piece = piece;
        updateIcon();
    }

    public void removePiece(){
        piece = null;
        updateIcon();
    }

    public void updateIcon(){
        if(piece == null){
            this.setIcon(new ImageIcon(
                    new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB)));
        }else{
            this.setIcon(new ImageIcon(ChessSprites.getSpriteByPiece(piece)));
        }
    }
}
