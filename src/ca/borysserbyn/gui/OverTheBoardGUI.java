package ca.borysserbyn.gui;

import ca.borysserbyn.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

public class OverTheBoardGUI {
    private JPanel gui;
    private JPanel chessBoard;
    private JPanel graveyardPanel;
    private TileButton originButton;
    private TileButton[][] tileButtons = new TileButton[8][8];
    private JScrollPane graveyardScroll;
    private Board board;
    private boolean isGameOver;
    private final JLabel message = new JLabel("Have fun!");

    OverTheBoardGUI() {
        board = new Board(1);
        System.out.println("Main board: " + board);
        initializeGui();
    }

    public final JComponent getChessBoard() {
        return chessBoard;
    }

    public final JComponent getGui() {
        return gui;
    }



    //Handles pieces/squares being clicked.
    public void clickTile(ActionEvent e){
        if(isGameOver){
            return;
        }
        TileButton selectedButton = (TileButton)e.getSource();
        Piece selectedPiece = selectedButton.getPiece();
        if(originButton == null && selectedPiece != null){ //is there a piece in the selected square and was a piece already selected
            if(selectedPiece.getColor() == board.getTurn()){//is it that colors turn to move
                originButton = selectedButton;
            }
        }else if(originButton != null){ //is there a piece to be moved
            if(selectedPiece == null){
                movePiece(selectedButton, originButton);
            }else if(selectedPiece.getColor() == originButton.getPiece().getColor()){
                originButton = selectedButton;
            }else{
                movePiece(selectedButton, originButton);
            }
        }
    }

    //Handles piece movement in the gui and calls it in the logic of the board.
    public void movePiece(TileButton selectedButton, TileButton originButton){
        Piece originPiece = originButton.getPiece();
        Tile destinationTile = selectedButton.getTile();
        if(board.isMoveLegal(originButton.getPiece(), selectedButton.getTile())){ //is the move legal
            board.movePiece(originPiece, destinationTile);
            initializePieces();

            if(board.getState() == BoardState.PROMOTING_AND_EATING || board.getState() == BoardState.PROMOTING_PAWN){
                displayPromotionWindow(selectedButton);
            }
            endGameMessage();
        }
        System.out.println(originPiece.getValue(board.getOrientation()));
        this.originButton = null;
    }

    //handles end game detection
    public void endGameMessage(){
        isGameOver = board.isGameOver();
        if(board.getState() == BoardState.CHECKMATE){
            JOptionPane.showMessageDialog(gui,"Checkmate!");
        }else if(board.getState() == BoardState.STALEMATE){
            JOptionPane.showMessageDialog(gui,"Stalemate!");

        }
    }

    //Popup that lets you choose which piece to promote a back rank pawn to
    public void displayPromotionWindow(TileButton selectedButton){
        String[] choices = { "QUEEN", "ROOK", "BISHOP", "KNIGHT", "PAWN"};
        String choice = (String) JOptionPane.showInputDialog(gui, "Choose a piece to promote to.",
                "Pawn promotion", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
        PieceName chosenPieceName = PieceName.valueOf(choice);
        board.promotePawn(selectedButton.getPiece(), chosenPieceName);
        selectedButton.updateIcon();

    }

    public void clickSaveButton(ActionEvent e){
        FileUtils.writeToFile(board);
    }

    public void clickLoadButton(ActionEvent e){
        board = FileUtils.readFile();
        board.getPieces().forEach(System.out::println);
        originButton = null;
        chessBoard.removeAll();
        initializeBoardSquares();
        initializePieces();
        gui.revalidate();
        gui.repaint();
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
        isGameOver = false;
        gui = new JPanel(new BorderLayout(3, 3));
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(this::clickSaveButton);
        tools.add(saveButton);
        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(this::clickLoadButton);
        tools.add(loadButton);
        tools.addSeparator();
        tools.add(new JButton("Resign")); // TODO - add functionality!
        tools.addSeparator();
        tools.add(message);


        originButton = null;

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
        for (int ii = 0; ii < tileButtons.length; ii++) {
            for (int jj = 0; jj < tileButtons[ii].length; jj++) {
                TileButton b = new TileButton(board.getTileByPosition(jj, ii));
                b.setMargin(buttonMargin);
                b.addActionListener(this::clickTile);
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
                tileButtons[jj][ii] = b;
            }
        }

        //fill the chess board
        chessBoard.add(new JLabel(""));
        // fill the top row
        for (int ii = 0; ii < 8; ii++) {
            chessBoard.add(
                    new JLabel("" + (ii),
                            SwingConstants.CENTER));
        }
        // fill the black non-pawn piece row
        for (int ii = 0; ii < 8; ii++) {
            for (int jj = 0; jj < 8; jj++) {
                switch (jj) {
                    case 0:
                        chessBoard.add(new JLabel("" + (ii),
                                SwingConstants.CENTER));
                    default:
                        chessBoard.add(tileButtons[jj][ii]);
                        chessBoard.repaint();
                }
            }
        }
    }

    public final void initializePieces(){
        for (int i = 0; i < tileButtons.length; i++) {
            for (int j = 0; j < tileButtons[i].length; j++) {
                TileButton pieceButton = tileButtons[i][j];
                Tile tile = pieceButton.getTile();
                Piece piece = board.getPieceByTile(tile);
                if(piece != null){
                    pieceButton.setPiece(piece);
                }else{
                    pieceButton.removePiece();
                }
            }
        }

        graveyardPanel.removeAll();
        for(Piece deadPiece : board.getEatenPieces()){
            sendPieceToGraveyard(deadPiece);
        }
    }
}
