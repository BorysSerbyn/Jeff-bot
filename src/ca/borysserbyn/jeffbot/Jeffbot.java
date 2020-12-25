package ca.borysserbyn.jeffbot;

import ca.borysserbyn.*;

import java.awt.*;
import java.util.Collections;

public class Jeffbot {
    private static int maxDepth = 2;
    private static int maxBreadth = -1;
    private Board board;
    private Color color;
    private Node currentNode;
    private Node baseNode;

    public Jeffbot(Color color) {
        this.color = color;
        this.board = new Board(1);
        this.baseNode = new Node(board, null, maxDepth, maxBreadth, color);
        currentNode = baseNode;
        initializeTree();
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

    public Move findBestMove() {
        Collections.sort(currentNode.getChildNodes());
        Node bestMoveNode = currentNode.getChildNodes().get(0);
        Move clonedMove = (Move) bestMoveNode.getLastMove().clone();
        System.out.println("Jeffs move: " + clonedMove + " with value: " + bestMoveNode.getCascadedPieceValue() + " (pieces:) " + bestMoveNode.getBoard().getPieces().size());
        updateTree(clonedMove);
        return clonedMove;
    }

    public void updateTree(Move move) {
        Move clonedMove = (Move) move.clone();
        Piece jeffPiece = board.getPieceByClone(move.getPiece());
        Tile jeffDestinationTile = board.getTileByClone(move.getTile());
        board.movePiece(jeffPiece, jeffDestinationTile);
        Node moveNode = getNodeByMove(currentNode, clonedMove);

        if (moveNode == null) {//is there a node in the tree corresponding the the move?
            System.out.println("Couldnt find node: " + move);
            Board clonedBoard = (Board) board.clone();
            moveNode = new Node(clonedBoard, currentNode, maxDepth, maxBreadth, color);
            currentNode.addChild(moveNode);
        }

        moveNode.addNodes(0);
        currentNode = moveNode;
        System.out.println("Updated current node: " + clonedMove + " which has " + currentNode.getChildNodes().size() + " children.");
    }


    public void initializeTree() {
        baseNode.addNodes(0);
    }
}
