package ca.borysserbyn.gui;

import ca.borysserbyn.Board;
import ca.borysserbyn.Piece;
import ca.borysserbyn.Tile;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class TileButton extends JButton {
    private Piece piece;
    private Tile tile;

    public TileButton(Tile tile) {
        this.tile = tile;
        piece = null;
    }

    public void displayPiece(){
        System.out.println(piece.toString());
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        updateIcon();
    }

    public void setAndMovePiece(Board board, Piece piece) {
        board.movePiece(piece, tile);
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
