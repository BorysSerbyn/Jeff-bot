package ca.borysserbyn.jeffbot;

import ca.borysserbyn.gui.ChessPanel;
import ca.borysserbyn.mechanics.ObservableGame;
import ca.borysserbyn.mechanics.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

public class Jeffbot {
    private int maxDepth = 5;
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
            System.out.print("Siblings: ");
            for (Node child : siblings) {
                if(child == bestNode){
                    System.out.print( ChessPanel.ANSI_RED+ child.getCascadedScore() + ", " +ChessPanel.ANSI_RESET);
                }else{
                    System.out.print(child.getCascadedScore() + ", ");
                }
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
        Move opponentMove;
        Node jeffMoveNode;

        try{
            opponentMove = game.getMoveByIndex(0);
            jeffMoveNode = currentNode.getChildNodes().get(0);
            currentNode = getNodeByMove(jeffMoveNode, opponentMove);
        }catch(Exception e){
            try{
                opponentMove = game.getMoveByIndex(0);
                currentNode = new Node(maxDepth, color, (Move) opponentMove.clone(), game.getTurnCounter());
            }catch(Exception e2){
                System.out.println("Couldnt find node");
                resetCurrentNode();
            }
        }
        resetCurrentNode();
        buildTree(currentNode, false);
    }

    public void buildTree(Node node, boolean secondTry) {

        /*int treeSize = node.addNodes(0, (Game) game.clone());
        System.out.println("Tree built with size: " + treeSize);*/
        Game clonedGame = (Game) game.clone();
        float[] positionResult = node.testMinimax(0, clonedGame, -10000, 10000);
        float positionScore = positionResult[0];
        float nodeCount = positionResult[1];
        System.out.println("Positions evaluated: " + nodeCount);
        System.out.println("Base score: " + positionScore + "\n");

        node.setParentNode(null);
        System.gc();
    }
}
