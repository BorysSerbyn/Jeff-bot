package ca.borysserbyn.gui;

import ca.borysserbyn.mechanics.*;
import ca.borysserbyn.jeffbot.Jeffbot;
import ca.borysserbyn.mechanics.Color;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class JeffGUI {
    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private JPanel chessBoard;
    private JPanel graveyardPanel;
    private TileButton originButton;
    private TileButton[][] tileButtons = new TileButton[8][8];
    private JScrollPane graveyardScroll;
    private int orientation = 1;
    private Game game;
    private boolean isGameOver;
    private Jeffbot jeff;
    private Color jeffColor = Color.BLACK;
    private final JLabel message = new JLabel("");

    JeffGUI() {
        initializeGui();
    }

    public final JComponent getChessBoard() {
        return chessBoard;
    }

    public final JComponent getGui() {
        return gui;
    }


    //Handles pieces/squares being clicked.
    public void clickTile(ActionEvent e) {
        if (isGameOver) {
            return;
        }
        if (jeffColor == game.getTurn()) {
            return;
        }
        TileButton selectedButton = (TileButton) e.getSource();
        Piece selectedPiece = selectedButton.getPiece();
        if (originButton == null && selectedPiece != null) { //is there a piece in the selected square and was a piece already selected
            if (selectedPiece.getColor() == game.getTurn()) {//is it that colors turn to move
                originButton = selectedButton;
            }
        } else if (originButton != null) { //is there a piece to be moved
            if (selectedPiece == null) {
                movePiece(selectedButton, originButton);
            } else if (selectedPiece.getColor() == originButton.getPiece().getColor()) {
                originButton = selectedButton;
            } else {
                movePiece(selectedButton, originButton);
            }
        }
    }

    //Handles piece movement in the gui and calls it in the logic of the board.
    public void movePiece(TileButton selectedButton, TileButton originButton) {
        Piece originPiece = originButton.getPiece();
        Move move = new Move(originPiece, selectedButton.getXOnBoard(), selectedButton.getYOnBoard());
        Move clonedMove = (Move) move.clone();
        Piece clonedPiece = clonedMove.getPiece();

        if (game.isMoveLegal(move)) { //is the move legal
            game.movePiece(move);
            initializePieces();
            endGameMessage();

            System.out.println("turn: " + game.getTurnCounter() + " state: " + game.getState());
            if (game.getState() == GameState.PROMOTING_AND_EATING || game.getState() == GameState.PROMOTING_PAWN) {
                displayPromotionWindow(selectedButton, clonedPiece);
            }
            if (!isGameOver) {
                long start_time = System.nanoTime();
                jeff.updateTree(clonedMove);
                jeffMove();
                long end_time = System.nanoTime();
                System.out.println("computing time " + (end_time - start_time) / 1e6);
            }
        }

        this.originButton = null;
    }

    //Handles bot movement.
    public void jeffMove() {
        System.out.println("These are the random seeds " + game.getSeed() + " " + jeff.getCurrentNode().getGame().getSeed());
        message.setText("Jeff is thinking.");
        Move bestMoveNodeBoard = jeff.findBestMove();
        Move bestMoveGUIBoard = game.getMoveByClone(bestMoveNodeBoard);
        Move bestMoveJeffBoard = jeff.getGame().getMoveByClone(bestMoveNodeBoard);
        game.movePiece(bestMoveGUIBoard);

        if (game.getState() == GameState.PROMOTING_AND_EATING || game.getState() == GameState.PROMOTING_PAWN) {
            game.promotePawn(bestMoveGUIBoard.getPiece(), PieceName.QUEEN);
            jeff.getGame().promotePawn(bestMoveJeffBoard.getPiece(), PieceName.QUEEN);
            bestMoveNodeBoard.getPiece().setPieceName(PieceName.QUEEN);
        }
        System.out.println(game.getState());
        initializePieces();
        endGameMessage();
        message.setText("Jeff is ready.");
        jeff.updateTree(bestMoveNodeBoard);
    }

    //handles end game detection
    public void endGameMessage() {
        isGameOver = game.isGameOver();
        if (game.getState() == GameState.CHECKMATE) {
            JOptionPane.showMessageDialog(gui, "Checkmate!");
        } else if (game.getState() == GameState.STALEMATE) {
            JOptionPane.showMessageDialog(gui, "Stalemate!");

        }
    }

    //Popup that lets you choose which piece to promote a back rank pawn to
    public void displayPromotionWindow(TileButton selectedButton, Piece clonedPiece) {
        String[] choices = {"QUEEN", "ROOK", "BISHOP", "KNIGHT", "PAWN"};
        String choice = (String) JOptionPane.showInputDialog(gui, "Choose a piece to promote to.",
                "Pawn promotion", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
        PieceName chosenPieceName = PieceName.valueOf(choice);
        game.promotePawn(selectedButton.getPiece(), chosenPieceName);
        Piece jeffPiece = jeff.getGame().getPieceByClone(clonedPiece);
        jeff.getGame().promotePawn(jeffPiece, chosenPieceName);
        clonedPiece.setPieceName(chosenPieceName);
        selectedButton.updateIcon();
    }

    public void clickSaveButton(ActionEvent e) {
        FileUtils.writeToFile(game);
    }

    public void clickRematchButton(ActionEvent e) {
        game = new Game(orientation);
        isGameOver = false;
        originButton = null;
        chessBoard.removeAll();
        initializeBoardSquares();
        initializePieces();
        gui.revalidate();
        gui.repaint();

        jeff.setBoard((Game) game.clone());
        if (jeffColor == game.getTurn()) {
            jeffMove();
        }
    }

    public void clickLoadButton(ActionEvent e) {
        Game newGame = FileUtils.readFile();
        if(newGame == null){
            return;
        }
        game = newGame;
        isGameOver = false;
        originButton = null;
        chessBoard.removeAll();
        initializeBoardSquares();
        initializePieces();
        gui.revalidate();
        gui.repaint();

        jeff.setBoard((Game) game.clone());
        if (jeffColor == game.getTurn()) {
            jeffMove();
        }
    }


    public void clickUndoButton(ActionEvent e) {
        if (game.getTurnCounter() > 2 && game.getTurn() != jeffColor) {//is there a move to go back to? is jeff done thinking?

            ArrayList<Move> moveHistory = game.getMoveHistory();

            Game newGame = new Game(orientation);
            newGame.setSeed(game.getSeed());
            //take two moves off the top, (yours and jeffs)
            for (int i = 0; i < moveHistory.size() - 2; i++) {
                Move move = newGame.getMoveByClone(moveHistory.get(i));
                newGame.movePiece(move);
            }

            game = newGame;
            isGameOver = false;
            originButton = null;
            chessBoard.removeAll();
            initializeBoardSquares();
            initializePieces();
            gui.revalidate();
            gui.repaint();

            jeff.setBoard((Game) game.clone());
        }
    }

    public void clickSeedButton(ActionEvent e){
        int newSeed = new Random().nextInt();
        game.setSeed(newSeed);
        jeff.setBoard((Game) game.clone());
    }

    //Sends piece to the gui graveyard (not the boards)
    public void sendPieceToGraveyard(Piece deadPiece) {
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
        game = new Game(orientation);
        jeff = new Jeffbot(jeffColor, game);

        isGameOver = false;
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        JButton rematchButton = new JButton("Rematch");
        rematchButton.addActionListener(this::clickRematchButton);
        tools.add(rematchButton);
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(this::clickUndoButton);
        tools.add(undoButton);
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(this::clickSaveButton);
        tools.add(saveButton);
        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(this::clickLoadButton);
        tools.add(loadButton);
        JButton changeSeedButton = new JButton("Change Seed");
        changeSeedButton.addActionListener(this::clickSeedButton);
        tools.add(changeSeedButton);
        tools.addSeparator();
        JButton helpButton = new JButton("Help");
        tools.add(helpButton);
        tools.addSeparator();
        tools.add(message);
        originButton = null;

        //add the chessboard to the gui
        chessBoard = new JPanel(new GridLayout(0, 9));
        chessBoard.setBorder(new LineBorder(java.awt.Color.BLACK));
        gui.add(chessBoard);

        //add the graveyard to the gui
        graveyardPanel = new JPanel();
        graveyardScroll = new JScrollPane(graveyardPanel);
        graveyardScroll.setPreferredSize(new Dimension(512, 100));
        gui.add(graveyardScroll, BorderLayout.SOUTH);

        initializeBoardSquares();

        initializePieces();

        //jeff makes the first move
        if (jeffColor == game.getTurn()) {
            jeffMove();
        }
    }

    public final void initializeBoardSquares() {
        Insets buttonMargin = new Insets(0, 0, 0, 0);
        for (int ii = 0; ii < tileButtons.length; ii++) {
            for (int jj = 0; jj < tileButtons[ii].length; jj++) {
                TileButton b = new TileButton(jj, ii);
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
                    b.setBackground(java.awt.Color.BLACK);
                } else {
                    b.setBackground(java.awt.Color.WHITE);
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
        for (int ii = 7; ii >= 0; ii--) {
            for (int jj = 0; jj < 8; jj++) {
                switch (jj) {
                    case 0:
                        chessBoard.add(new JLabel("" + Math.abs(ii),
                                SwingConstants.CENTER));
                    default:
                        chessBoard.add(tileButtons[jj][ii]);
                        chessBoard.repaint();
                }
            }
        }
    }
    public final void initializePieces() {
        for (int i = 0; i < tileButtons.length; i++) {
            for (int j = 0; j < tileButtons[i].length; j++) {
                TileButton pieceButton = tileButtons[i][j];
                Piece piece = game.getPieceByTile(i, j);
                if (piece != null) {
                    pieceButton.setPiece(piece);
                } else {
                    pieceButton.removePiece();
                }
            }
        }

        graveyardPanel.removeAll();
        for (Piece deadPiece : game.getEatenPieces()) {
            sendPieceToGraveyard(deadPiece);
        }

        gui.revalidate();
    }
}
