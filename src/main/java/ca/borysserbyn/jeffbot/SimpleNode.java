package ca.borysserbyn.jeffbot;

import ca.borysserbyn.mechanics.*;

import java.util.ArrayList;

public class SimpleNode {

    protected int maxDepth;
    protected Move move;
    protected Color color;
    protected ArrayList<SimpleNode> childNodes;

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
            SimpleNode childNode = new SimpleNode(maxDepth, color, possibleMove);
            positionsFound += childNode.addNodes(depth + 1, clonedGame);
            this.addChild(childNode);
        }

        if(depth == 1){
            //System.out.println(move.toSFNotation() + ": " + positionsFound);
        }

        return positionsFound;
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            Game game = NotationUtils.createGameFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            SimpleNode node = new SimpleNode(i, Color.WHITE, null);
            long start_time = System.nanoTime();
            int positionsFound = node.addNodes(0, game);
            long end_time = System.nanoTime();
            System.out.println("Depth: " + i + " Result: " + positionsFound + " Time: " + (end_time - start_time) / 1e6);
        }
    }
}
