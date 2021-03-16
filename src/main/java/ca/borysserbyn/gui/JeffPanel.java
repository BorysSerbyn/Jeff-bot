package ca.borysserbyn.gui;

import ca.borysserbyn.mechanics.*;
import ca.borysserbyn.jeffbot.Jeffbot;
import ca.borysserbyn.mechanics.Color;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import static java.awt.event.ActionEvent.ACTION_PERFORMED;

public class JeffPanel extends ChessPanel{
    private Jeffbot jeff;
    private Color jeffColor = Color.WHITE;

    public JeffPanel(Game game) {
        super(game);
        addSeedButton();
        initializeJeff();
    }

    @Override
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

    @Override
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

            System.out.println();
            System.out.println(ANSI_RED + "###############################################" + ANSI_RESET);
            System.out.println("turn: " + game.getTurnCounter());
            System.out.println("state: " + game.getState());

            if (game.getState() == GameState.PROMOTING_AND_EATING || game.getState() == GameState.PROMOTING_PAWN) {
                displayPromotionWindow(selectedButton, clonedPiece);
            }

            if (!isGameOver) {

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
        game.promotePawn(selectedButton.getPiece(), chosenPieceName);
        Piece jeffPiece = jeff.getGame().getPieceByClone(clonedPiece);
        jeff.getGame().promotePawn(jeffPiece, chosenPieceName);
        clonedPiece.setPieceName(chosenPieceName);
        selectedButton.updateIcon();
    }

    @Override
    public void clickRematchButton(ActionEvent e) {
        game = new Game(game.getOrientation());
        isGameOver = false;
        originButton = null;
        chessBoard.removeAll();
        initializeBoardSquares();
        initializePieces();
        this.revalidate();
        this.repaint();

        jeff.setBoard((Game) game.clone());
        if (jeffColor == game.getTurn()) {
            jeff.movePiece(this);
        }
    }

    @Override
    public void clickLoadButton(ActionEvent e) {
        Game newGame = FileUtils.readFile();
        if (newGame == null) {
            return;
        }
        game = newGame;
        isGameOver = false;
        originButton = null;
        chessBoard.removeAll();
        initializeBoardSquares();
        initializePieces();
        this.revalidate();
        this.repaint();

        jeff.setBoard((Game) game.clone());
        if (jeffColor == game.getTurn()) {
            jeff.movePiece(this);
        }
    }

    @Override
    public void clickUndoButton(ActionEvent e) {
        if (game.getTurnCounter() > 2 && game.getTurn() != jeffColor) {//is there a move to go back to? is jeff done thinking?

            ArrayList<Move> moveHistory = game.getMoveHistory();

            Game newGame = new Game(game.getOrientation());
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
            this.revalidate();
            this.repaint();

            jeff.setBoard((Game) game.clone());
        }
    }

    public void clickSeedButton(ActionEvent e) {
        int newSeed = new Random().nextInt();
        game.setSeed(newSeed);
        jeff.setBoard((Game) game.clone());
    }

    public void addSeedButton(){
        JButton changeSeedButton = new JButton("Change Seed");
        changeSeedButton.addActionListener(this::clickSeedButton);
        tools.add(changeSeedButton);
        tools.addSeparator();
    }

    public void initializeJeff(){
        jeff = new Jeffbot(jeffColor, game);
        if (jeffColor == game.getTurn()) {
            jeff.movePiece(this);
        }
    }
}
