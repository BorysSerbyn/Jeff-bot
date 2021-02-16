package ca.borysserbyn.gui;

import ca.borysserbyn.*;
import ca.borysserbyn.Color;
import ca.borysserbyn.jeffbot.Jeffbot;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class JeffGUI {
    private StaticGUI staticGUI;
    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private JPanel chessBoard;
    private JPanel graveyardPanel;
    private TileButton originButton;
    private TileButton[][] tileButtons = new TileButton[8][8];
    private JScrollPane graveyardScroll;
    private int orientation = 0;
    private Board board;
    private boolean isGameOver;
    private Jeffbot jeff;
    private Color jeffColor = Color.WHITE;
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

    public StaticGUI getStaticGUI() {
        return staticGUI;
    }

    //Handles pieces/squares being clicked.
    public void clickTile(ActionEvent e) {
        if (isGameOver) {
            return;
        }
        if (jeffColor == board.getTurn()) {
            return;
        }
        TileButton selectedButton = (TileButton) e.getSource();
        Piece selectedPiece = selectedButton.getPiece();
        if (originButton == null && selectedPiece != null) { //is there a piece in the selected square and was a piece already selected
            if (selectedPiece.getColor() == board.getTurn()) {//is it that colors turn to move
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

        if (board.isMoveLegal(move)) { //is the move legal
            board.movePiece(move);
            initializePieces();
            endGameMessage();

            System.out.println(board.getState());
            if (board.getState() == BoardState.PROMOTING_AND_EATING || board.getState() == BoardState.PROMOTING_PAWN) {
                displayPromotionWindow(selectedButton, clonedPiece);
            }
            if (!isGameOver) {
                long start_time = System.nanoTime();
                jeff.updateTree(clonedMove);
                jeffMove();
                long end_time = System.nanoTime();
                System.out.println(board.getTurnCounter() + " " + (end_time - start_time) / 1e6);
            }
        }

        this.originButton = null;
    }

    //Handles bot movement.
    public void jeffMove() {
        System.out.println("These are the random seeds " + board.getSeed() + " " + jeff.getCurrentNode().getBoard().getSeed());
        message.setText("Jeff is thinking.");
        Move bestMoveNodeBoard = jeff.findBestMove();
        Move bestMoveGUIBoard = board.getMoveByClone(bestMoveNodeBoard);
        Move bestMoveJeffBoard = jeff.getBoard().getMoveByClone(bestMoveNodeBoard);
        board.movePiece(bestMoveGUIBoard);

        if (board.getState() == BoardState.PROMOTING_AND_EATING || board.getState() == BoardState.PROMOTING_PAWN) {
            board.promotePawn(bestMoveGUIBoard.getPiece(), PieceName.QUEEN);
            jeff.getBoard().promotePawn(bestMoveJeffBoard.getPiece(), PieceName.QUEEN);
            bestMoveNodeBoard.getPiece().setPieceName(PieceName.QUEEN);
        }
        System.out.println(board.getState());
        initializePieces();
        endGameMessage();
        message.setText("Jeff is ready.");
        jeff.updateTree(bestMoveNodeBoard);
    }

    //handles end game detection
    public void endGameMessage() {
        isGameOver = board.isGameOver();
        if (board.getState() == BoardState.CHECKMATE) {
            JOptionPane.showMessageDialog(gui, "Checkmate!");
        } else if (board.getState() == BoardState.STALEMATE) {
            JOptionPane.showMessageDialog(gui, "Stalemate!");

        }
    }

    //Popup that lets you choose which piece to promote a back rank pawn to
    public void displayPromotionWindow(TileButton selectedButton, Piece clonedPiece) {
        String[] choices = {"QUEEN", "ROOK", "BISHOP", "KNIGHT", "PAWN"};
        String choice = (String) JOptionPane.showInputDialog(gui, "Choose a piece to promote to.",
                "Pawn promotion", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
        PieceName chosenPieceName = PieceName.valueOf(choice);
        board.promotePawn(selectedButton.getPiece(), chosenPieceName);
        Piece jeffPiece = jeff.getBoard().getPieceByClone(clonedPiece);
        jeff.getBoard().promotePawn(jeffPiece, chosenPieceName);
        clonedPiece.setPieceName(chosenPieceName);
        selectedButton.updateIcon();
    }

    public void clickSaveButton(ActionEvent e) {
        FileUtils.writeToFile(board);
    }

    public void clickRematchButton(ActionEvent e) {
        board = new Board(orientation);
        isGameOver = false;
        originButton = null;
        chessBoard.removeAll();
        initializeBoardSquares();
        initializePieces();
        gui.revalidate();
        gui.repaint();

        jeff.setBoard((Board) board.clone());
        if (jeffColor == board.getTurn()) {
            jeffMove();
        }
    }

    public void clickLoadButton(ActionEvent e) {
        Board newBoard = FileUtils.readFile();
        if(newBoard == null){
            return;
        }
        board = newBoard;
        isGameOver = false;
        originButton = null;
        chessBoard.removeAll();
        initializeBoardSquares();
        initializePieces();
        gui.revalidate();
        gui.repaint();

        jeff.setBoard((Board) board.clone());
        if (jeffColor == board.getTurn()) {
            jeffMove();
        }
    }


    public void clickUndoButton(ActionEvent e) {
        if (board.getTurnCounter() > 2 && board.getTurn() != jeffColor) {//is there a move to go back to? is jeff done thinking?

            ArrayList<Move> moveHistory = board.getMoveHistory();

            Board newBoard = new Board(orientation);
            newBoard.setSeed(board.getSeed());
            //take two moves off the top, (yours and jeffs)
            for (int i = 0; i < moveHistory.size() - 2; i++) {
                Move move = newBoard.getMoveByClone(moveHistory.get(i));
                newBoard.movePiece(move);
            }

            board = newBoard;
            isGameOver = false;
            originButton = null;
            chessBoard.removeAll();
            initializeBoardSquares();
            initializePieces();
            gui.revalidate();
            gui.repaint();

            jeff.setBoard((Board) board.clone());
        }
    }

    public void clickSeedButton(ActionEvent e){
        int newSeed = new Random().nextInt();
        board.setSeed(newSeed);
        jeff.setBoard((Board) board.clone());
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
        board = new Board(orientation);
        jeff = new Jeffbot(jeffColor, board);
        this.staticGUI = new StaticGUI(jeff.getBoard());
        System.out.println("Jeff's board" + jeff.getBoard());

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
        if (jeffColor == board.getTurn()) {
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

    public final void initializePieces() {
        for (int i = 0; i < tileButtons.length; i++) {
            for (int j = 0; j < tileButtons[i].length; j++) {
                TileButton pieceButton = tileButtons[i][j];
                Piece piece = board.getPieceByTile(i, j);
                if (piece != null) {
                    pieceButton.setPiece(piece);
                } else {
                    pieceButton.removePiece();
                }
            }
        }

        graveyardPanel.removeAll();
        for (Piece deadPiece : board.getEatenPieces()) {
            sendPieceToGraveyard(deadPiece);
        }

        staticGUI.initializePieces();
        gui.revalidate();
    }
}
