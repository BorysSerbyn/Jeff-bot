package ca.borysserbyn.gui;

import ca.borysserbyn.mechanics.*;
import ca.borysserbyn.jeffbot.Jeffbot;
import ca.borysserbyn.mechanics.Color;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class JeffPanel extends ChessPanel implements Observer {
    private Jeffbot jeff;
    private Color jeffColor = Color.BLACK;

    public JeffPanel(Game game) {
        super(game);
        addSeedButton();
        initializeJeff();
    }

    @Override
    public void update(Observable source, Object arg1){
        initializeJeff();
        chessBoard.removeAll();
        initializeBoardSquares();
        initializePieces();
        revalidate();
        repaint();
    }


    @Override
    //Handles pieces/squares being clicked.
    public void clickTile(ActionEvent e) {
        if (isGameOver) {
            return;
        }
        if (jeffColor == observableGame.getGame().getTurn()) {
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

    @Override
    //Handles piece movement in the gui and calls it in the logic of the board.
    public void movePiece(TileButton selectedButton, TileButton originButton) {
        Piece originPiece = originButton.getPiece();
        Move move = new Move(originPiece, selectedButton.getXOnBoard(), selectedButton.getYOnBoard());
        Move clonedMove = (Move) move.clone();
        Piece clonedPiece = clonedMove.getPiece();

        if (observableGame.getGame().isMoveLegal(move)) { //is the move legal
            observableGame.movePiece(move);
            endGameMessage();
            System.out.println("turn: " + observableGame.getGame().getTurnCounter());
            System.out.println("state: " + observableGame.getGame().getState());
            System.out.println(ANSI_RED + "###############################################" + ANSI_RESET);
            System.out.println();

            if (observableGame.getGame().getState() == GameState.PROMOTING_AND_EATING || observableGame.getGame().getState() == GameState.PROMOTING_PAWN) {
                displayPromotionWindow(selectedButton, clonedPiece);
            }
            System.out.println();
        }

        this.originButton = null;
    }

    @Override
    //Popup that lets you choose which piece to promote a back rank pawn to
    public void displayPromotionWindow(TileButton selectedButton, Piece clonedPiece) {
        String[] choices = {"QUEEN", "ROOK", "BISHOP", "KNIGHT", "PAWN"};
        String choice = (String) JOptionPane.showInputDialog(this, "Choose a piece to promote to.",
                "Pawn promotion", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
        PieceName chosenPieceName = PieceName.valueOf(choice);
        observableGame.getGame().promotePawn(selectedButton.getPiece(), chosenPieceName);
        Piece jeffPiece = jeff.getGame().getPieceByClone(clonedPiece);
        jeff.getGame().promotePawn(jeffPiece, chosenPieceName);
        clonedPiece.setPieceName(chosenPieceName);
        selectedButton.updateIcon();
    }

    @Override
    public void clickRematchButton(ActionEvent e) {
        setGame(new Game(observableGame.getGame().getOrientation()));
        isGameOver = false;
        originButton = null;
    }

    @Override
    public void clickLoadButton(ActionEvent e) {
        Game newGame = FileUtils.readSerializedGame();
        if (newGame == null) {
            return;
        }
        setGame(newGame);
        isGameOver = false;
        originButton = null;
    }

    @Override
    public void clickUndoButton(ActionEvent e) {
        if (observableGame.getGame().getTurnCounter() > 2 && observableGame.getGame().getTurn() != jeffColor) {//is there a move to go back to? is jeff done thinking?

            ArrayList<Move> moveHistory = observableGame.getGame().getMoveHistory();

            Game newGame = new Game(observableGame.getGame().getOrientation());
            newGame.setSeed(observableGame.getGame().getSeed());
            //take two moves off the top, (yours and jeffs)
            for (int i = 0; i < moveHistory.size() - 2; i++) {
                Move move = newGame.getMoveByClone(moveHistory.get(i));
                newGame.movePiece(move);
            }

            setGame(newGame);
            isGameOver = false;
            originButton = null;
        }
    }

    public void clickSeedButton(ActionEvent e) {
        int newSeed = new Random().nextInt();
        observableGame.getGame().setSeed(newSeed);
        jeff.getGame().setSeed(newSeed);
    }

    public void addSeedButton(){
        JButton changeSeedButton = new JButton("Change Seed");
        changeSeedButton.addActionListener(this::clickSeedButton);
        tools.add(changeSeedButton);
        tools.addSeparator();
    }

    public void initializeJeff(){
        if(jeff == null){
            jeff = new Jeffbot(jeffColor, observableGame.getGame());
        }
        if (jeffColor == observableGame.getGame().getTurn() && !isGameOver) {
            long start_time = System.nanoTime();
            jeff.setGame(observableGame.getGame());
            Move bestMove = observableGame.getGame().getMoveByClone(jeff.findBestMove());
            observableGame.getGame().movePiece(bestMove);
            long end_time = System.nanoTime();
            System.out.println("calculation time: " + (end_time - start_time) / 1e6);
        }
    }
}
