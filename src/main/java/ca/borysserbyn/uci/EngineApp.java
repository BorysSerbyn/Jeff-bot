package ca.borysserbyn.uci;

import ca.borysserbyn.jeffbot.Jeffbot;
import ca.borysserbyn.mechanics.Game;
import ca.borysserbyn.mechanics.Move;
import ca.borysserbyn.mechanics.NotationUtils;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class EngineApp {

    private Game game = new Game(1);
    private Jeffbot jeff = new Jeffbot(game.getTurn(), game, 5, false);
    private ArrayList<String> expectedOpts = new ArrayList<>();

    public static void main(String[] args) {
        EngineApp engineApp = new EngineApp();
        Scanner scanner = new Scanner(System.in);
        while(true){
            String command = scanner.nextLine();
            if(command.equals("exit")){
                break;
            }
            String[] commandArgs = command.split(" ");
            engineApp.run(commandArgs, command);
        }
    }

    public void run(String[] args, String command) {

        //args = new String[]{"position", "fen", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"};
        CommandLine line = parseArguments(args);

        if (line.hasOption("uci")) {
            sendResponse("id name Jeff_bot");
            sendResponse("id author Borys Serbyn");
            sendResponse("uciok");
        }else if(line.hasOption("isready")){
            sendResponse("readyok");
        }else if(line.hasOption("position") && line.hasOption("startpos")){
            game = NotationUtils.createGameFromFen("8/8/R3N3/1n2N3/k7/8/5PPP/7K b - - 10 55");
        }else if(line.hasOption("position") && line.hasOption("fen")){
            String[] fenArray = Arrays.copyOfRange(args, 2, args.length);
            String fenStr = "";
            for(String fenArg : fenArray){
                fenStr += fenArg + " ";
            }
            game = NotationUtils.createGameFromFen(fenStr);
        }else if(line.hasOption("stop")){
            Move bestMove = jeff.findBestMove();
            sendResponse("bestmove " + bestMove.toUciNotation());
        }else if(line.hasOption("ucinewgame")){
            game = new Game(1);
        }else if(line.hasOption("go")){
            jeff.setColor(game.getTurn());
            jeff.searchGame(game);
            Move bestMove = jeff.findBestMove();
            Move gameMove = game.getMoveByClone(bestMove);
            String uciMove = bestMove.toUciNotation();
            game.movePiece(gameMove);
            sendResponse("bestmove " + uciMove);
        }else {
            //printAppHelp();
        }

        List<String> argsList = Arrays.asList(args);
        if(Arrays.asList(args).contains("-moves")){
            int movesIndex = argsList.indexOf("-moves");
            List<String> movesList = argsList.subList(movesIndex+1, argsList.size());
            for(String uciMove: movesList){
                Move move = NotationUtils.movefromUciNotation(uciMove, game);
                game.movePiece(move);
            }
            game.getMoveHistory().forEach(System.out::println);
        }
    }

    private void sendResponse(String response){
        System.out.println(response);
    }

    private CommandLine parseArguments(String[] args) {

        Options options = getOptions();
        CommandLine line = null;


        for (int i = 0; i < args.length; i++) {
            if(expectedOpts.contains(args[i])){
                args[i] = "-" + args[i] ;
            }
        }

        CommandLineParser parser = new DefaultParser();

        try {
            line = parser.parse(options, args);

        } catch (ParseException ex) {

            System.err.println("Failed to parse command line arguments");
            System.err.println(ex.toString());
            printAppHelp();

            System.exit(1);
        }

        return line;
    }

    private Options getOptions() {

        var options = new Options();
        expectedOpts.add("uci");
        expectedOpts.add("isready");
        expectedOpts.add("position");
        expectedOpts.add("fen");
        expectedOpts.add("startpos");
        expectedOpts.add("go");
        expectedOpts.add("stop");
        expectedOpts.add("moves");

        //options.addOption("f", "filename", true, "file name to load data from");
        options.addOption("moves", true, "moves piece");
        options.addOption("uci", false, "does engine support uci");
        options.addOption("isready", false, "is bot ready");
        options.addOption("position", false, "load position");
        options.addOption("fen", true, "load position from fen");
        options.addOption("startpos", false, "load start position");
        options.addOption("go", false, "search from current position");
        options.addOption("depth", true, "search up to depth");
        options.addOption("stop", false, "stop search");

        return options;
    }

    private void printAppHelp() {

        Options options = getOptions();

        var formatter = new HelpFormatter();
        formatter.printHelp("Jeff-bot", options, true);
    }
}
