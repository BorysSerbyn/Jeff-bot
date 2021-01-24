package ca.borysserbyn;

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

    public static void writeToFile(Board board) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(savedGamesDir);
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try{
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
                bw.write(objetToJson(board));
                bw.close();
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public static Board readFile(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(savedGamesDir);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try{
                BufferedReader br = new BufferedReader(new FileReader(file));
                Board board = (Board) (new Gson().fromJson(br, Board.class)).clone();
                return board;
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

}
