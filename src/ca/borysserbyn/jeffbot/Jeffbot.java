package ca.borysserbyn.jeffbot;

import ca.borysserbyn.*;
import ca.borysserbyn.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

public class Jeffbot {
    private static int maxDepth = 5;
    private static int maxBreadth = 20;
    private static final ForkJoinPool pool = new ForkJoinPool();
    private Board board;
    private Color color;
    private Node currentNode;

    public Jeffbot(Color color, Board board) {
        this.color = color;
        this.board = (Board) board.clone();
        currentNode = new Node(board, null, maxDepth, maxBreadth, color);
        buildTree(currentNode, false);
    }

    public void setBoard(Board board) {
        this.board = board;
        resetCurrentNode();
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

    public void resetCurrentNode() {
        currentNode = new Node(board, null, maxDepth, maxBreadth, color);
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public Move findBestMove() {
        Collections.sort(currentNode.getChildNodes());
        Node bestMoveNode = currentNode.getChildNodes().get(0);
        if (bestMoveNode.getCascadedScore() + 1 < currentNode.getCurrentScore()) {
            secondTry();
            bestMoveNode = currentNode.getChildNodes().get(0);
        }
        printThoughtProcess(bestMoveNode);
        return bestMoveNode.getLastMove();
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

    public void updateTree(Move move) {
        Move clonedMove = (Move) move.clone();
        Move moveJeffBoard = board.getMoveByClone(move);

        board.movePiece(moveJeffBoard);
        Node moveNode = getNodeByMove(currentNode, clonedMove);

        if (moveNode == null) {//is there a node in the tree corresponding the the move?
            System.out.println("Couldnt find node: " + move + " in tree.");
            Board clonedBoard = (Board) board.clone();
            resetCurrentNode();
        }else{
            currentNode = moveNode;
        }
        buildTree(currentNode, false);
    }

    public void buildTree(Node node, boolean secondTry) {
        //node.addNodes(0, maxDepth, false);
        TreeTask rootTask = new TreeTask(node, 0, secondTry);
        pool.invoke(rootTask);
        node.getChildNodes().forEach(Node::inheritChildScore);
        node.setParentNode(null);
        System.gc();
    }
}
