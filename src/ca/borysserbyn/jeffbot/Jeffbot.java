package ca.borysserbyn.jeffbot;

import ca.borysserbyn.*;

import java.awt.*;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

public class Jeffbot {
    private static int maxDepth = 4;
    private static int maxBreadth = 20;
    private static int maxRetries = 10;
    private Board board;
    private Color color;
    private Node currentNode;

    public Jeffbot(Color color) {
        this.color = color;
        this.board = new Board(1);
        currentNode = new Node(board, null, maxDepth, maxBreadth, color, maxRetries);
        buildTree(currentNode);
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

        currentNode.getChildNodes().forEach(System.out::println);
        System.out.println();
        bestMoveNode.getChildNodes().forEach(System.out::println);
        System.out.println("Jeffs move: " + bestMoveNode);

        return bestMoveNode.getLastMove();
    }

    public void resetTree(){
        System.out.println("Reseting the tree, no good moves found");
        currentNode.removeAllChildren();
        buildTree(currentNode);
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
            moveNode = new Node(clonedBoard, currentNode, maxDepth, maxBreadth, color, maxRetries);
            currentNode.addChild(moveNode);
        }

        currentNode = moveNode;
        buildTree(currentNode);
        //System.out.println("Updated current node: " + clonedMove + " which has " + currentNode.getChildNodes().size() + " children.");
    }

    public void buildTree(Node node){
        TreeTask rootTask = new TreeTask(node, 0);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(rootTask);
        for (Node childNode : node.getChildNodes()) {
            childNode.inheritGameOutcome();
        }
        node.setParentNode(null);
        //node.inheritGameOutcome();
    }
}
