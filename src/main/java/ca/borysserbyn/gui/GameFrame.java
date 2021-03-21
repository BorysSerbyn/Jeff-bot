package ca.borysserbyn.gui;

import ca.borysserbyn.mechanics.Color;
import ca.borysserbyn.mechanics.Game;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class GameFrame extends JFrame{

    JPanel newGamePanel = new JPanel();
    private Color color;
    private int difficulty;
    private JComboBox difficultyDropDown;
    private JComboBox colorDropDown;

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
        setColor();
        setDifficulty();
        JeffPanel jeffPanel = new JeffPanel(new Game(1), color, difficulty);
        changePanel(jeffPanel);
    }

    public void overTheButtonClick(ActionEvent e){
        setColor();
        setDifficulty();
        ChessPanel chessPanel = new ChessPanel(new Game(1), color);
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

        String[] colorsArray = {"White", "Black"};
        colorDropDown = new JComboBox(colorsArray);
        colorDropDown.setSelectedIndex(0);


        String[] difficultiesArray = {"4 depth", "5 depth"};
        difficultyDropDown = new JComboBox(difficultiesArray);
        difficultyDropDown.setSelectedIndex(0);


        newGamePanel.add(colorDropDown);
        newGamePanel.add(difficultyDropDown);


        this.add(newGamePanel);
    }

    public void setColor(){
        String colorStr = (String) colorDropDown.getSelectedItem();
        if(colorStr.equals("White")){
            color = Color.WHITE;
        }else{
            color = Color.BLACK;
        }
    }

    public void setDifficulty(){
        String diffStr =  ((String) difficultyDropDown.getSelectedItem()).substring(0,1);
        difficulty = Integer.valueOf(diffStr);
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
