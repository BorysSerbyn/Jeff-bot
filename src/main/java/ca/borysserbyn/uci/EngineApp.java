package ca.borysserbyn.jeffbot;

import ca.borysserbyn.mechanics.Game;
import ca.borysserbyn.mechanics.Move;
import ca.borysserbyn.mechanics.NotationUtils;
import org.apache.commons.cli.*;

import java.util.ArrayList;

public class EngineApp {

    Game game = new Game(1);
    Jeffbot jeff = new Jeffbot(game.getTurn(), game);

    public static void main(String[] args) {
        EngineApp engineApp = new EngineApp();
        engineApp.run(args);
    }

    public void run(String[] args) {

        args = new String[]{"-position", "-fen", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"};
        CommandLine line = parseArguments(args);

        if (line.hasOption("uci")) {
            sendResponse("id name Jeff_bot");
            sendResponse("id author Borys Serbyn");
            sendResponse("uciok");
        }else if(line.hasOption("isready")){
            sendResponse("readyok");
        }else if(line.hasOption("position") && line.hasOption("startpos")){
            game = new Game(1);
        }else if(line.hasOption("position") && line.hasOption("fen")){
            String fenStr = line.getOptionValue("fen");
            game = NotationUtils.createGameFromFen(fenStr);
        }else if(line.hasOption("go")){
            jeff.setColor(game.getTurn());
            jeff.setMaxDepth(4);
            jeff.setGame(game);
            Move bestMove = jeff.findBestMove();
            sendResponse(bestMove.toSFNotation());
        }else if(line.hasOption("stop")){
            Move bestMove = jeff.findBestMove();
            sendResponse(bestMove.toSFNotation());
        }else if(line.hasOption("ucinewgame")){
            game = new Game(1);
        }else {
            printAppHelp();
        }

    }

    private void sendResponse(String response){
        System.out.println(response);
    }

    private CommandLine parseArguments(String[] args) {

        Options options = getOptions();
        CommandLine line = null;

        CommandLineParser parser = new CommandLineParser() {
            @Override
            public CommandLine parse(Options options, String[] strings) throws ParseException {
                CommandLine line = CommandLine.Builder.build();

                getOptions().getOption()
            }

            @Override
            public CommandLine parse(Options options, String[] strings, boolean b) throws ParseException {
                return null;
            }
        };

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

        //options.addOption("f", "filename", true, "file name to load data from");
        options.addOption("uci", false, "does engine support uci");
        options.addOption("isready", false, "is bot ready");
        options.addOption("position", false, "load position");
        options.addOption("fen", true, "load position from fen");
        options.addOption("startpos", false, "load start position");
        options.addOption("go", true, "search from current position");
        options.addOption("stop", false, "stop search");
        return options;
    }

    private void printAppHelp() {

        Options options = getOptions();

        var formatter = new HelpFormatter();
        formatter.printHelp("Jeff-bot", options, true);
    }
}
