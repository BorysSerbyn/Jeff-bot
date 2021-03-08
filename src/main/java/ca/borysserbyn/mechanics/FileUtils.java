package ca.borysserbyn.mechanics;

import com.google.gson.Gson;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileUtils {
    private static File savedGamesDir = new File("saved_games");
    private static File savedTrees = new File("saved_trees");

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


    public static void writeToFile(String moves) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(savedTrees);
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try{
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
                bw.write(moves);
                bw.close();
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public static String readTreeFile(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(savedTrees);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try{
                return Files.readString(file.toPath(), StandardCharsets.US_ASCII);
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        }
        return null;
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
