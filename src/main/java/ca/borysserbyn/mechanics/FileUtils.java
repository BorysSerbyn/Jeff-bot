package ca.borysserbyn.mechanics;

import com.google.gson.Gson;

import javax.swing.*;
import java.io.*;

public class FileUtils {
    private static File savedGamesDir = new File("saved_games");

    public FileUtils() {
    }

    public static String objetToJson(Object o){
        return new Gson().toJson(o);
    }

    public static void writeToFile(Game game) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(savedGamesDir);
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try{
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
                bw.write(objetToJson(game));
                bw.close();
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public static Game readFile(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(savedGamesDir);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try{
                BufferedReader br = new BufferedReader(new FileReader(file));
                Game game = (Game) (new Gson().fromJson(br, Game.class)).clone();
                return game;
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

}
