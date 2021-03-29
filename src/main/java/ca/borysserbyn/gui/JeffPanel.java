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
    private Color jeffColor;
    private int maxDepth;

    public JeffPanel(Game game, Color playerColor, int maxDepth) {
        super(game, playerColor);
        jeffColor = playerColor == Color.WHITE ? Color.BLACK : Color.WHITE;
        this.maxDepth = maxDepth;
        //addSeedButton();
        initializeJeff();
        chessBoard.removeAll();
        initializeBoardSquares();
        initializePieces();
        revalidate();
        repaint();
    }

    @Override
    public void update(Observable source, Object arg1) {
        initializeJeff();
        chessBoard.removeAll();
        initializeBoardSquares();
        initializePieces();
        revalidate();
        repaint();
        System.out.println("turn: " + observableGame.getGame().getTurnCounter());
        System.out.println("state: " + observableGame.getGame().getState());
        System.out.println(ANSI_RED + "###############################################" + ANSI_RESET);
        System.out.println();
    }


    @Override
    //Handles pieces/squares being clicked.
    public void clickTile(ActionEvent e) {
        if (jeffColor == observableGame.getGame().getTurn()) {
            return;
        }
        super.clickTile(e);
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
            isGameOver = false;
            originButton = null;
            setGame(newGame);
        }
    }

    @Override
    public void clickCopyPgn(ActionEvent e) {
        String black;
        String white;
        if (jeffColor == Color.WHITE) {
            white = "Jeffbot";
            black = "Anonymous";
        } else {
            black = "Jeffbot";
            white = "Anonymous";
        }
        String str = NotationUtils.gameToPGN(observableGame.getGame(), white, black);
        copyToClipBoard(str);
    }

    public void clickSeedButton(ActionEvent e) {
        int newSeed = new Random().nextInt();
        observableGame.getGame().setSeed(newSeed);
        jeff.getGame().setSeed(newSeed);
    }

    public void addSeedButton() {
        JButton changeSeedButton = new JButton("Change Seed");
        changeSeedButton.addActionListener(this::clickSeedButton);
        tools.add(changeSeedButton);
        tools.addSeparator();
    }

    public void initializeJeff() {
        if (jeff == null) {
            jeff = new Jeffbot(jeffColor, observableGame.getGame(), maxDepth);
        }

        if (jeffColor == observableGame.getGame().getTurn() && !observableGame.getGame().isGameOver()) {
            jeff.searchGame(observableGame.getGame());
            Move bestMove = observableGame.getGame().getMoveByClone(jeff.findBestMove());
            observableGame.getGame().movePiece(bestMove);
        }
    }
}
