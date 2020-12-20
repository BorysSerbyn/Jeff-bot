package ca.borysserbyn;

import java.io.*;

public class FileUtils {
    private static File savedBoardsFile = new File("C:\\Users\\BOBO\\Documents\\Jeff-bot\\Saved_Boards.bin");

    public FileUtils() {
    }

    public static void deleteSavedGames(){
        try{
            PrintWriter writer = new PrintWriter(savedBoardsFile);
            writer.print("");
            writer.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public static void writeToFile(Board board) {

        try{
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("Saved_Boards.bin"));
            objectOutputStream.writeObject(board);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public static Board readFile(){
        try{
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(savedBoardsFile));
            try{
                Board board = (Board) objectInputStream.readObject();
                return board;
            }catch(ClassNotFoundException e){
                System.out.println(e.getMessage());
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

}
