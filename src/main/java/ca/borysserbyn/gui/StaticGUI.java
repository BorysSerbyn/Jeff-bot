package ca.borysserbyn.gui;

import ca.borysserbyn.mechanics.Game;
import ca.borysserbyn.mechanics.Piece;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class StaticGUI {
    private Game game;
    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private TileButton[][] pieceButtonArray = new TileButton[8][8];
    private JPanel chessBoard;
    private JPanel graveyardPanel;
    private JScrollPane graveyardScroll;
    private static final String COLS = "ABCDEFGH";

    StaticGUI(Game game) {
        this.game = game;
        initializeGui();
    }

    public final JComponent getChessBoard() {
        return chessBoard;
    }

    public final JComponent getGui() {
        return gui;
    }

    //Sends piece to the gui graveyard (not the boards)
    public void sendPieceToGraveyard(Piece deadPiece){
        JLabel deadPieceLabel = new JLabel("");
        deadPieceLabel.setIcon(new ImageIcon(ChessSprites.getSpriteByPiece(deadPiece)));
        graveyardPanel.add(deadPieceLabel);
        graveyardPanel.revalidate();
        graveyardPanel.repaint();
    }



    /*
    Initializes the gui with a new board
     */
    public final void initializeGui() {
        // set up the main gui
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));

        //add the graveyard to the gui
        graveyardPanel = new JPanel();
        graveyardScroll = new JScrollPane(graveyardPanel);
        graveyardScroll.setPreferredSize(new Dimension(512, 100));
        gui.add(graveyardScroll, BorderLayout.SOUTH);

        //add the chessboard to the gui
        chessBoard = new JPanel(new GridLayout(0, 9));
        chessBoard.setBorder(new LineBorder(java.awt.Color.BLACK));
        gui.add(chessBoard);

        initializeBoardSquares();

        initializePieces();
    }

    public final void initializeBoardSquares(){
        Insets buttonMargin = new Insets(0,0,0,0);
        for (int ii = 0; ii < pieceButtonArray.length; ii++) {
            for (int jj = 0; jj < pieceButtonArray[ii].length; jj++) {
                TileButton b = new TileButton(jj, ii);
                b.setMargin(buttonMargin);
                // our chess pieces are 64x64 px in size, so we'll
                // 'fill this in' using a transparent icon..
                ImageIcon icon = new ImageIcon(
                        new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
                b.setIcon(icon);
                if ((jj % 2 == 1 && ii % 2 == 1)
                        //) {
                        || (jj % 2 == 0 && ii % 2 == 0)) {
                    b.setBackground(java.awt.Color.WHITE);
                } else {
                    b.setBackground(java.awt.Color.BLACK);
                }
                pieceButtonArray[jj][ii] = b;
            }
        }



        //fill the chess board
        chessBoard.add(new JLabel(""));
        // fill the top row
        for (int ii = 7; ii >= 0; ii--) {
            chessBoard.add(
                    new JLabel(COLS.substring(ii, ii+1),
                            SwingConstants.CENTER));
        }
        // fill the black non-pawn piece row
        for (int ii = 0; ii < 8; ii++) {
            for (int jj = 0; jj < 8; jj++) {
                switch (jj) {
                    case 0:
                        chessBoard.add(new JLabel("" + (ii + 1),
                                SwingConstants.CENTER));
                    default:
                        chessBoard.add(pieceButtonArray[jj][ii]);
                        chessBoard.repaint();
                }
            }
        }
    }

    public final void initializePieces(){
        for (int i = 0; i < pieceButtonArray.length; i++) {
            for (int j = 0; j < pieceButtonArray[i].length; j++) {
                TileButton pieceButton = pieceButtonArray[i][j];
                Piece piece = game.getPieceByTile(i, j);
                if(piece != null){
                    pieceButton.setPiece(piece);
                }else{
                    pieceButton.removePiece();
                }
            }
        }

        graveyardPanel.removeAll();
        for(Piece deadPiece : game.getEatenPieces()){
            sendPieceToGraveyard(deadPiece);
        }

    }

}
