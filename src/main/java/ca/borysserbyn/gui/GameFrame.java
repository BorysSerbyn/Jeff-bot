package ca.borysserbyn.gui;

import ca.borysserbyn.mechanics.Game;
import ca.borysserbyn.mechanics.NotationUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class GameFrame extends JFrame{

    JPanel newGamePanel = new JPanel();

    public GameFrame(String title){
        super(title);
        initializeNewGameGUI();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationByPlatform(true);
        this.pack();
        this.setMinimumSize(this.getSize());
        this.setVisible(true);
    }


    public void jeffButtonClick(ActionEvent e){
        JeffPanel jeffPanel = new JeffPanel(new Game(1));
        changePanel(jeffPanel);
    }

    public void overTheButtonClick(ActionEvent e){
        ChessPanel chessPanel = new ChessPanel(new Game(1));
        changePanel(chessPanel);
    }

    public void changePanel(JPanel newPanel){
        this.remove(newGamePanel);
        this.add(newPanel);
        revalidate();
        repaint();
        pack();
        setMinimumSize(getSize());
    }


    public void initializeNewGameGUI(){
        JButton jeffButton = new JButton("Play against Jeff.");
        JButton overTheButton = new JButton("Play over the board.");
        jeffButton.addActionListener(this::jeffButtonClick);
        overTheButton.addActionListener(this::overTheButtonClick);
        newGamePanel.add(jeffButton);
        newGamePanel.add(overTheButton);
        this.add(newGamePanel);
    }

    public static final void createJFrame(JComponent jComponent){
        JFrame f = new JFrame("Jeff Bot");
        f.add(jComponent);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLocationByPlatform(true);
        f.pack();
        f.setMinimumSize(f.getSize());
        f.setVisible(true);
    }



    public static void main(String[] args) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                GameFrame newGame = new GameFrame("New Game");
            }
        };
        SwingUtilities.invokeLater(r);
    }
}
