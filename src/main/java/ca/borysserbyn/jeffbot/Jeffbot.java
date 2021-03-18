package ca.borysserbyn.jeffbot;

import ca.borysserbyn.gui.ChessPanel;
import ca.borysserbyn.mechanics.ObservableGame;
import ca.borysserbyn.mechanics.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

public class Jeffbot {
    private int maxDepth = 4;
    private static final ForkJoinPool pool = new ForkJoinPool();
    private Game game;
    private Color color;
    private Node currentNode;

    public Jeffbot(Color color, Game game) {
        this.color = color;
        this.game = (Game) game.clone();
    }

    public Jeffbot(Color color, Game game, int maxDepth) {
        this.maxDepth = maxDepth;
        this.color = color;
        this.game = (Game) game.clone();
    }

    public void setGame(Game game) {
        this.game = game;
        updateTree();
    }

    public Game getGame() {
        return game;
    }

    public Node getNodeByMove(Node node, Move move) {
        return node.getChildNodes().stream()
                .filter(child -> child.getMove().equals(move))
                .findFirst()
                .orElse(null);
    }

    public void resetCurrentNode() {
        currentNode = new Node(maxDepth, color, null, game.getTurnCounter());
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public Move findBestMove() {
        Collections.sort(currentNode.getChildNodes());
        Node bestMoveNode = currentNode.getChildNodes().get(0);
        printThoughtProcess(bestMoveNode);
        return bestMoveNode.getMove();
    }

    public void printThoughtProcess(Node bestMoveNode) {
        Node bestNode = bestMoveNode;
        while (true) {
            ArrayList<Node> siblings = bestNode.getParentNode().getChildNodes();
            Collections.sort(siblings);
            for (Node child : siblings) {
                System.out.print(child.getCascadedScore() + ", ");
            }
            System.out.println();
            System.out.println(bestNode);
            System.out.println();
            if (bestNode.getChildNodes().isEmpty()) {
                break;
            }
            Collections.sort(bestNode.getChildNodes());
            bestNode = bestNode.getChildNodes().get(0);
        }
    }

    public void secondTry() {
        System.out.println("Reseting the tree, no good moves found");
        resetCurrentNode();
        buildTree(currentNode, true);
        Collections.sort(currentNode.getChildNodes());
    }

    public void updateTree() {
        Move jeffMove;
        Move opponentMove;
        Node jeffMoveNode;
        Node opponentMoveNode;

        if(game.getTurnCounter() == 0){
            opponentMoveNode = null;
        }else if(game.getTurnCounter() == 1){
            opponentMove = game.getMoveByIndex(0);
            opponentMoveNode = new Node(maxDepth, color, (Move) opponentMove.clone(), game.getTurnCounter());
        }else{
            jeffMove = game.getMoveByIndex(1);
            opponentMove = game.getMoveByIndex(0);
            jeffMoveNode = currentNode.getChildNodes().get(0);
            opponentMoveNode = getNodeByMove(jeffMoveNode, opponentMove);
        }

        if (opponentMoveNode == null) {//is there a node in the tree corresponding to the move?
            System.out.println("Couldnt find node: " + opponentMoveNode);
            resetCurrentNode();
        }else{
            currentNode = opponentMoveNode;
        }
        buildTree(currentNode, false);
    }

    public void buildTree(Node node, boolean secondTry) {

        int treeSize = node.addNodes(0, (Game) game.clone());
        System.out.println("Tree built with size: " + treeSize);
        node.setParentNode(null);
        System.gc();
    }
}
