package ca.borysserbyn.gui;

import ca.borysserbyn.mechanics.Game;

import java.awt.*;
import java.awt.event.ActionEvent;

public class TestPanel extends ChessPanel{
    private static TestPanel singletonInstance;

    public TestPanel(Game game){
        super(game);
    }

    @Override
    public void clickTile(ActionEvent e){
        System.out.println("cant move in test panel");
    }

    public void hilightTileRed(int x, int y){
        tileButtons[x][y].setBackground(java.awt.Color.red);
    }

    public void hilightTileBlue(int x, int y){
        tileButtons[x][y].setBackground(Color.blue);
    }

    public void hilightTileGreen(int x, int y){
        tileButtons[x][y].setBackground(Color.green);
    }

    public static TestPanel getSingletonInstance(){
        if(singletonInstance == null){
            singletonInstance = new TestPanel(new Game(1));
        }
        return singletonInstance;
    }

    public void setGame(Game newGame){
        game = newGame;
        chessBoard.removeAll();
        initializeBoardSquares();
        initializePieces();
        this.revalidate();
        this.repaint();
    }
}
