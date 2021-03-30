package ca.borysserbyn.jeffbot;

import ca.borysserbyn.gui.ChessPanel;
import ca.borysserbyn.mechanics.Color;
import ca.borysserbyn.mechanics.Game;
import ca.borysserbyn.mechanics.Move;


import java.util.ArrayList;
import java.util.Collections;

public class Jeffbot {
    private int maxDepth = 4;
    private Game game;
    private Color color;
    private Node currentNode;
    private boolean debugMode = true;

    public Jeffbot(Color color, Game game) {
        this.color = color;
        this.game = (Game) game.clone();
    }

    public Jeffbot(Color color, Game game, int maxDepth) {
        this.maxDepth = maxDepth;
        this.color = color;
        this.game = (Game) game.clone();
    }

    public Jeffbot(Color color, Game game, int maxDepth, boolean debugMode) {
        this.debugMode = debugMode;
        this.maxDepth = maxDepth;
        this.color = color;
        this.game = (Game) game.clone();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void searchGame(Game game) {
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
        if(debugMode){
            printThoughtProcess(bestMoveNode);
        }
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
                //System.out.println("Couldnt find node");
                resetCurrentNode();
            }
        }
        resetCurrentNode();
        buildTree(currentNode, false);
    }

    public void buildTree(Node node, boolean secondTry) {

        /*int treeSize = node.addNodes(0, (Game) game.clone(), false);
        System.out.println("Tree built with size: " + treeSize);*/

        Game clonedGame = (Game) game.clone();


        long start_time = System.nanoTime();
        float[] positionResult = node.miniMaxSearch(0, clonedGame, -10000, 10000);
        long end_time = System.nanoTime();

        if(debugMode){
            float positionScore = positionResult[0];
            float nodeCount = positionResult[1];
            System.out.println("calculation time: " + (end_time - start_time) / 1e6);
            System.out.println("Positions evaluated: " + nodeCount);
            System.out.println("Base score: " + positionScore + "\n");
        }

        node.setParentNode(null);
        System.gc();
    }

    public static void main(String[] args) {
        Game game = new Game(1);
        Color color = Color.WHITE;
        int difficulty = 6;
        Jeffbot jeff = new Jeffbot(color, game, difficulty, true);
        jeff.searchGame(game);
    }
}
