package ca.borysserbyn.jeffbot;

import ca.borysserbyn.*;

import java.awt.*;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

public class Jeffbot {
    private static int maxDepth = 4;
    private static int maxBreadth = 30;
    private static final ForkJoinPool pool = new ForkJoinPool();
    private Board board;
    private Color color;
    private Node currentNode;

    public Jeffbot(Color color) {
        this.color = color;
        this.board = new Board(1);
        currentNode = new Node(board, null, maxDepth, maxBreadth, color);
        buildTree(currentNode, false);
    }

    public Board getBoard() {
        return board;
    }

    public Node getNodeByMove(Node node, Move move) {
        return node.getChildNodes().stream()
                .filter(child -> child.getLastMove().equals(move))
                .findFirst()
                .orElse(null);
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public Move findBestMove() {
        Collections.sort(currentNode.getChildNodes());
        Node bestMoveNode = currentNode.getChildNodes().get(0);
        if(bestMoveNode.getCascadedScore() + 1 < currentNode.getCurrentScore()){
            secondTry();
            bestMoveNode = currentNode.getChildNodes().get(0);
        }
        printThoughtProcess(bestMoveNode);
        return bestMoveNode.getLastMove();
    }

    public void printThoughtProcess(Node bestMoveNode){
        currentNode.getChildNodes().forEach(System.out::println);
        System.out.println();
        bestMoveNode.getChildNodes().forEach(System.out::println);
        System.out.println("Jeffs move: " + bestMoveNode);
    }

    public void secondTry(){
        System.out.println("Reseting the tree, no good moves found");
        currentNode.removeAllChildren();
        buildTree(currentNode, true);
        Collections.sort(currentNode.getChildNodes());
    }

    public void updateTree(Move move) {
        Move clonedMove = (Move) move.clone();
        Piece jeffPiece = board.getPieceByClone(move.getPiece());
        Tile jeffDestinationTile = board.getTileByClone(move.getTile());

        board.movePiece(jeffPiece, jeffDestinationTile);
        Node moveNode = getNodeByMove(currentNode, clonedMove);

        if (moveNode == null) {//is there a node in the tree corresponding the the move?
            System.out.println("Couldnt find node: " + move + " in tree.");
            Board clonedBoard = (Board) board.clone();
            moveNode = new Node(clonedBoard, currentNode, maxDepth, maxBreadth, color);
            currentNode.addChild(moveNode);
        }

        currentNode = moveNode;
        buildTree(currentNode, false);
    }

    public void buildTree(Node node, boolean secondTry){
        //node.addNodes(0, maxDepth, false);
        TreeTask rootTask = new TreeTask(node, 0, secondTry);
        pool.invoke(rootTask);
        node.getChildNodes().forEach(Node::inheritChildScore);
        node.setParentNode(null);
        System.gc();
    }
}
