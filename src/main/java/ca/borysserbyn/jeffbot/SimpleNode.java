package ca.borysserbyn.jeffbot;

import ca.borysserbyn.gui.GameGUI;
import ca.borysserbyn.gui.TestPanel;
import ca.borysserbyn.mechanics.*;

import java.util.ArrayList;

public class SimpleNode {

    private int maxDepth;
    private Move move;
    private Color color;
    private ArrayList<SimpleNode> childNodes;

    public SimpleNode(int maxDepth, Color color, Move move) {
        this.color = color;
        this.move = move;
        this.maxDepth = maxDepth;
        childNodes = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Node{" +
                ", node move: " + move.toString() +
                '}';
    }

    public void addChild(SimpleNode childNode) {
        childNodes.add(childNode);
    }

    public boolean isPromoting(Game game) {
        return game.getState() == GameState.PROMOTING_AND_EATING || game.getState() == GameState.PROMOTING_PAWN;
    }


    public int addNodes(int depth, Game game) {
        if (depth >= maxDepth) {
            return 1;
        }
        ArrayList<Move> allMovesList = game.generateLegalMovesByColor(game.getTurn());
        int positionsFound = 0;
        for (Move possibleMove : allMovesList) {
            Game clonedGame = (Game) game.clone();
            Move clonedMove = clonedGame.getMoveByClone(possibleMove);
            clonedGame.movePiece(clonedMove);

            if (isPromoting(clonedGame)) {
                clonedGame.promotePawn(clonedMove.getPiece(), PieceName.QUEEN);
            }
            SimpleNode childNode = new SimpleNode(maxDepth, color, possibleMove);
            positionsFound += childNode.addNodes(depth + 1, clonedGame);
            this.addChild(childNode);
        }

        if(depth == 1){
            //System.out.println(move.toSFNotation() + ": " + positionsFound);
        }

        return positionsFound;
    }

    public String addNodes2(int depth, Game game) {
        if (depth >= maxDepth) {
            return move.toString();
        }
        ArrayList<Move> allMovesList = game.generateLegalMovesByColor(game.getTurn());
        String positionsFound = "";
        if (move != null) {
            positionsFound += move.toString()+ ":     ";
        }
        for (Move possibleMove : allMovesList) {
            Game clonedGame = (Game) game.clone();
            Move clonedMove = clonedGame.getMoveByClone(possibleMove);
            clonedGame.movePiece(clonedMove);

            if (isPromoting(clonedGame)) {
                clonedGame.promotePawn(clonedMove.getPiece(), PieceName.QUEEN);
            }
            SimpleNode childNode = new SimpleNode(maxDepth, color, possibleMove);

            positionsFound += childNode.addNodes2(depth + 1, clonedGame) + ", ";
            this.addChild(childNode);
        }
        positionsFound += "\n";
        return positionsFound;
    }

    public static void main(String[] args) {
//        Game game = FenUtils.createGameFromFen("rnbg1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
//        SimpleNode node = new SimpleNode(2, Color.WHITE, null);
//        String moves = node.addNodes2(0, game);
//        FileUtils.writeToFile(moves);

        //Game game = FenUtils.createGameFromFen("rnbg1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");

        /*for (int i = 1; i <= 4; i++) {
            Game game = FenUtils.createGameFromFen("rnQq1k1r/pp2bppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R b KQ - 1 8");
            SimpleNode node = new SimpleNode(i, Color.WHITE, null);
            long start_time = System.nanoTime();
            int positionsFound = node.addNodes(0, game);
            long end_time = System.nanoTime();
            System.out.println("Depth: " + i + " Result: " + positionsFound + " Time: " + (end_time - start_time) / 1e6);
        }*/
/*
        TestPanel testPanel = TestPanel.getSingletonInstance();
        GameGUI.createJFrame(testPanel);*/

        for (int i = 1; i <= 5; i++) {
            Game game = FenUtils.createGameFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

            SimpleNode node = new SimpleNode(i, Color.WHITE, null);
            long start_time = System.nanoTime();
            int positionsFound = node.addNodes(0, game);
            long end_time = System.nanoTime();
            System.out.println("Depth: " + i + " Result: " + positionsFound + " Time: " + (end_time - start_time) / 1e6);
        }
    }
}
