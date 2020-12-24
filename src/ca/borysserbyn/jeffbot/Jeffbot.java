package ca.borysserbyn.jeffbot;
import ca.borysserbyn.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Jeffbot {
    private static int maxDepth = 3;
    private static int maxBreadth = 7;
    private Board board;
    private Color color;
    private Node currentNode;
    private Node baseNode;
    private int turnCounter;

    public Jeffbot(Color color) {
        this.color = color;
        this.board = new Board(1);
        this.baseNode = new Node(0, null, null, maxDepth, maxBreadth, color);
        currentNode = baseNode;
        initializeTree();
    }

    public Board getBoard() {
        return board;
    }

    public Node getNodeByMove(Node node, Move move){
        return node.getChildrenNodes().stream()
                .filter(child -> child.getMove().equals(move))
                .findFirst()
                .orElse(null);
    }

    public Move findBestMove(){
        Collections.sort(currentNode.getChildrenNodes());
        Node bestMoveNode = currentNode.getChildrenNodes().get(0);
        Move clonedMove = (Move) bestMoveNode.getMove().clone();
        System.out.println("Jeffs move: " + clonedMove);
        updateTree(bestMoveNode.getMove());
        return clonedMove;
    }

    public void updateTree(Move move){
        Move clonedMove = (Move) move.clone();
        Piece jeffPiece = board.getPieceByClone(move.getPiece());
        Tile jeffDestinationTile = board.getTileByClone(move.getTile());
        board.movePiece(jeffPiece, jeffDestinationTile);
        turnCounter = board.getTurnCounter();
        Node moveNode = getNodeByMove(currentNode, clonedMove);

        if(moveNode == null){//is there a node in the tree corresponding the the move?
            System.out.println("Couldnt find node: " + move);
            moveNode = new Node(turnCounter, clonedMove, currentNode, maxDepth, maxBreadth, color);
            currentNode.addChild(moveNode);
        }

        moveNode.addNodes(board, 0);
        currentNode = moveNode;
        System.out.println("Updated current node: " + clonedMove + " which has " + currentNode.getChildrenNodes().size() + " children.");
    }


    public void initializeTree(){
        baseNode.addNodes(board, 0);
    }
}
