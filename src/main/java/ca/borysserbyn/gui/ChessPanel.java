package ca.borysserbyn.gui;
import ca.borysserbyn.mechanics.*;
import ca.borysserbyn.mechanics.Color;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ChessPanel extends JPanel implements Observer {
    protected JPanel chessBoard;
    protected JPanel graveyardPanel;
    protected TileButton originButton;
    protected TileButton[][] tileButtons = new TileButton[8][8];
    protected JScrollPane graveyardScroll;
    protected ObservableGame observableGame;
    protected boolean isGameOver;
    protected final JLabel message = new JLabel("Have fun!");
    protected JToolBar tools;
    protected int[] yCoords;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public ChessPanel(Game game, Color color) {
        super(new BorderLayout(3, 3));
        yCoords = color == Color.BLACK ? new int[]{0,1,2,3,4,5,6,7} : new int[]{7,6,5,4,3,2,1,0};
        observableGame = new ObservableGame(game);
        observableGame.addObserver(this);
        tools = new JToolBar();
        initializeGui();
    }

    @Override
    public void update(Observable source, Object arg1){
        chessBoard.removeAll();
        initializeBoardSquares();
        initializePieces();
        this.revalidate();
        this.repaint();
    }

    public Game getGame() {
        return observableGame.getGame();
    }

    public void setGame(Game game) {
        observableGame.setGameSync(game);
    }

    //Handles pieces/squares being clicked.
    public void clickTile(ActionEvent e) {
        if (isGameOver) {
            return;
        }
        TileButton selectedButton = (TileButton) e.getSource();
        Piece selectedPiece = selectedButton.getPiece();
        if (originButton == null && selectedPiece != null) { //is there a piece in the selected square and was a piece already selected
            if (selectedPiece.getColor() == observableGame.getGame().getTurn()) {//is it that colors turn to move
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

    //Handles piece movement in the this and calls it in the logic of the board.
    public void movePiece(TileButton selectedButton, TileButton originButton) {
        Piece originPiece = originButton.getPiece();
        Move move = new Move(originButton.getPiece(), selectedButton.getXOnBoard(), selectedButton.getYOnBoard());

        if (observableGame.getGame().isMoveLegal(move)) { //is the move legal
            if(move.getPiece().getPieceName() == PieceName.PAWN){
                if (observableGame.getGame().isPawnPromotionLegal(move)) {
                    displayPromotionWindow(selectedButton, move);
                }
            }
            observableGame.movePiece(move);
            endGameMessage();
        }
        this.originButton = null;
    }

    //Popup that lets you choose which piece to promote a back rank pawn to
    public void displayPromotionWindow(TileButton selectedButton, Move move) {
        System.out.println("promoting: " + move);
        String[] choices = {"QUEEN", "ROOK", "BISHOP", "KNIGHT", "PAWN"};
        String choice = (String) JOptionPane.showInputDialog(this, "Choose a piece to promote to.",
                "Pawn promotion", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
        PieceName chosenPieceName = PieceName.valueOf(choice);
        move.setPromotionSnapShot(chosenPieceName);
    }

    //handles end game detection
    public void endGameMessage() {
        isGameOver = observableGame.getGame().isGameOver();
        if (observableGame.getGame().getState() == GameState.CHECKMATE) {
            JOptionPane.showMessageDialog(this, "Checkmate!");
        } else if (observableGame.getGame().getState() == GameState.STALEMATE) {
            JOptionPane.showMessageDialog(this, "Stalemate!");

        }
    }

    public void clickSaveButton(ActionEvent e) {
        FileUtils.writeToFile(observableGame.getGame());
    }

    public void clickRematchButton(ActionEvent e) {
        setGame(new Game(observableGame.getGame().getOrientation()));
        isGameOver = false;
        originButton = null;
    }

    public void clickLoadButton(ActionEvent e) {
        Game newGame = FileUtils.readSerializedGame();
        if(newGame == null){
            return;
        }
        setGame(newGame);
        isGameOver = false;
        originButton = null;
    }

    public void clickCopyFen(ActionEvent e) {
        String str = NotationUtils.createFenFromGame(observableGame.getGame());
        copyToClipBoard(str);
    }

    public void clickCopyPgn(ActionEvent e) {
        String str = NotationUtils.gameToPGN(observableGame.getGame());
        copyToClipBoard(str);
    }

    public void copyToClipBoard(String str){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection strSel = new StringSelection(str);
        clipboard.setContents(strSel, null);
    }


    public void clickUndoButton(ActionEvent e) {
        ArrayList<Move> moveHistory = observableGame.getGame().getMoveHistory();
        Game newGame = new Game(observableGame.getGame().getOrientation());
        newGame.setSeed(observableGame.getGame().getSeed());
        for (int i = 0; i < moveHistory.size() - 1; i++) {
            Move move = newGame.getMoveByClone(moveHistory.get(i));
            newGame.movePiece(move);
        }
        setGame(newGame);
        isGameOver = false;
        originButton = null;
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
    Initializes the this with a new board
     */
    public final void initializeGui() {
        // set up the main this
        isGameOver = false;
        this.setBorder(new EmptyBorder(5, 5, 5, 5));
        tools.setFloatable(false);
        this.add(tools, BorderLayout.PAGE_START);
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
        JButton copyFENButton = new JButton("Copy FEN");
        copyFENButton.addActionListener(this::clickCopyFen);
        tools.add(copyFENButton);
        JButton copyPGNButton = new JButton("Copy PGN");
        copyPGNButton.addActionListener(this::clickCopyPgn);
        tools.add(copyPGNButton);


        tools.addSeparator();
        JButton helpButton = new JButton("Help");
        tools.add(helpButton);
        tools.addSeparator();
        tools.add(message);
        originButton = null;


        originButton = null;

        //add the graveyard to the this
        graveyardPanel = new JPanel();
        graveyardScroll = new JScrollPane(graveyardPanel);
        graveyardScroll.setPreferredSize(new Dimension(512, 100));
        this.add(graveyardScroll, BorderLayout.SOUTH);

        //add the chessboard to the this
        chessBoard = new JPanel(new GridLayout(0, 9));
        chessBoard.setBorder(new LineBorder(java.awt.Color.BLACK));
        this.add(chessBoard);

        initializeBoardSquares();
        initializePieces();
    }

    public final void initializeBoardSquares() {
        Insets buttonMargin = new Insets(0, 0, 0, 0);
        for (int y = 0; y < tileButtons.length; y++) {
            for (int x = 0; x < tileButtons[y].length; x++) {
                TileButton b = new TileButton(x, y);
                b.setMargin(buttonMargin);
                b.addActionListener(this::clickTile);
                // our chess pieces are 64x64 px in size, so we'll
                // 'fill this in' using a transparent icon..
                ImageIcon icon = new ImageIcon(
                        new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
                b.setIcon(icon);
                if ((x % 2 == 1 && y % 2 == 1)
                        //) {
                        || (x % 2 == 0 && y % 2 == 0)) {
                    b.setBackground(java.awt.Color.BLACK);
                } else {
                    b.setBackground(java.awt.Color.WHITE);
                }
                tileButtons[x][y] = b;
            }
        }

        chessBoard.add(new JLabel(""));

        for (int ii = 0; ii < 8; ii++) {
            chessBoard.add(
                    new JLabel("" + (ii),
                            SwingConstants.CENTER));
        }

        for (int y: yCoords) {
            for (int x = 0; x < 8; x++) {
                switch (x) {
                    case 0:
                        chessBoard.add(new JLabel("" + Math.abs(y),
                                SwingConstants.CENTER));
                    default:
                        chessBoard.add(tileButtons[x][y]);
                        chessBoard.repaint();
                }
            }
        }
    }

    public final void initializePieces() {
        for (int x = 0; x < tileButtons.length; x++) {
            for (int y = 0; y < tileButtons[x].length; y++) {
                TileButton pieceButton = tileButtons[x][y];
                //Piece piece = game.getBoard()[i][j];
                Piece piece = observableGame.getGame().getPieceByTile(x, y);
                if (piece != null) {
                    pieceButton.setPiece(piece);
                } else {
                    pieceButton.removePiece();
                }
            }
        }

        graveyardPanel.removeAll();
        for (Piece deadPiece : observableGame.getGame().getEatenPieces()) {
            sendPieceToGraveyard(deadPiece);
        }
    }
}
