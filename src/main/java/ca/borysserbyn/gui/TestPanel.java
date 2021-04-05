package ca.borysserbyn.gui;

import ca.borysserbyn.mechanics.Game;

import java.awt.*;
import java.awt.event.ActionEvent;

public class TestPanel extends ChessPanel{
    private static TestPanel singletonInstance;

    public TestPanel(Game game, ca.borysserbyn.mechanics.Color color){
        super(game, color);
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
            singletonInstance = new TestPanel(new Game(1), ca.borysserbyn.mechanics.Color.WHITE);
        }
        return singletonInstance;
    }

    public static void displayTestPanel(){
        TestPanel testPanel = TestPanel.getSingletonInstance();
        GameFrame.createJFrame(testPanel);
    }

    public static void pauseAndView(Game game){
        TestPanel testPanel = TestPanel.getSingletonInstance();
        testPanel.setTestGame(game);
        try{
            Thread.sleep(3000);
        }catch(Exception e){
            System.out.println("test panel pause and view failed");
            e.printStackTrace();
        }
    }

    public void setTestGame(Game newGame){
        observableGame.setGame(newGame);
        chessBoard.removeAll();
        initializeBoardSquares();
        initializePieces();
        this.revalidate();
        this.repaint();
    }
}
