package ca.borysserbyn.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class NewGameGUI extends JFrame{

    public NewGameGUI(String title){
        super(title);
        initializeNewGameGUI();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationByPlatform(true);
        this.pack();
        this.setMinimumSize(this.getSize());
        this.setVisible(true);
    }


    public void jeffButtonClick(ActionEvent e){
        JeffGUI jeffGUI = new JeffGUI();
        createJFrame(jeffGUI.getGui());
        //createJFrame(jeffGUI.getStaticGUI().getGui());
        this.dispose();
    }

    public void overTheButtonClick(ActionEvent e){
        OverTheBoardGUI overTheBoardGUI = new OverTheBoardGUI(1);
        createJFrame(overTheBoardGUI.getGui());
        this.dispose();
    }


    public void initializeNewGameGUI(){
        JPanel pannel = new JPanel();
        JButton jeffButton = new JButton("Play against Jeff.");
        JButton overTheButton = new JButton("Play over the board.");
        jeffButton.addActionListener(this::jeffButtonClick);
        overTheButton.addActionListener(this::overTheButtonClick);
        pannel.add(jeffButton);
        pannel.add(overTheButton);
        this.add(pannel);
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
                NewGameGUI newGame = new NewGameGUI("New Game");
            }
        };
        SwingUtilities.invokeLater(r);
    }
}
