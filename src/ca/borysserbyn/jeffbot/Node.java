package ca.borysserbyn.jeffbot;

import ca.borysserbyn.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Node implements Comparable {
    private int maxDepth;
    private int maxBreadth;
    private Color color;
    private Board board;
    private double currentPieceValue;
    private double cascadedPieceValue;

    private double checkmateProb;
    private double stalemateProb;
    private ArrayList<Node> childNodes;
    private Node parentNode;


    public Node(Board board, Node parentNode, int maxDepth, int maxBreadth, Color color) {
        this.color = color;
        this.maxBreadth = maxBreadth;
        this.maxDepth = maxDepth;
        this.board = board;
        this.parentNode = parentNode;
        cascadedPieceValue = 0;
        currentPieceValue = 0;
        checkmateProb = 0;
        stalemateProb = 0;
        childNodes = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Node{" +
                "turnCount=" + board.getTurnCounter() +
                ", move=" + getLastMove().toString() +
                ", value=" + getCascadedPieceValue() +
                '}';
    }

    //not final at all
    @Override
    public int compareTo(Object o) {
        Node nodeToCompare = (Node) o;
        //double checkmateIncrease = (this.checkmateProb - nodeToCompare.checkmateProb) / 1;
        //double pieceValueIncrease = (this.cascadedPieceValue - nodeToCompare.cascadedPieceValue) / 38;
        double totalIncrease = nodeToCompare.cascadedPieceValue - this.cascadedPieceValue;
        return (int) totalIncrease;
    }

    public double getCurrentPieceValue() {
        return currentPieceValue;
    }

    public Board getBoard() {
        return board;
    }

    public Move getLastMove() {
        return board.getLastMove();
    }

    public ArrayList<Node> getChildNodes() {
        return childNodes;
    }

    public void addChild(Node childNode) {
        childNodes.add(childNode);
    }

    public void removeAllChildren() {
        childNodes = new ArrayList<Node>();
    }

    public double getCascadedPieceValue() {
        return cascadedPieceValue;
    }

    public void setCascadedPieceValue(){
        cascadedPieceValue = childNodes.get(0).getCascadedPieceValue();
        for (Node childNode : childNodes) {
            double childPieceValue = childNode.getCascadedPieceValue();
            if(board.getTurn() == color){ //if its your turn; choose the best outcome
                cascadedPieceValue = childPieceValue > cascadedPieceValue ? childPieceValue : cascadedPieceValue;
            }else{// if not; choose the worst one
                cascadedPieceValue = childPieceValue < cascadedPieceValue ? childPieceValue : cascadedPieceValue;
            }
        }
    }

    //recusively populates board tree with outcomes
    public void addNodes(int depth) {
        currentPieceValue = board.getBoardValueByColor(color);
        if (board.isGameOver() || depth >= maxDepth) {//is game over or desired depth reached?
            cascadedPieceValue = currentPieceValue;
            if (board.getState() == BoardState.CHECKMATE) {
                checkmateProb = 1;
            } else if (board.getState() == BoardState.STALEMATE) {
                stalemateProb = 1;
            }
            return; // exits before it can try to extend the branch infinitely
        }

        if (!this.getChildNodes().isEmpty()) {//does this node already have children?
            for (Node childNode : this.getChildNodes()) {
                childNode.addNodes(depth + 1);
            }
        } else {
            ArrayList<Board> legalBoards = board.getLegalBoardsByColor(board.getTurn());
            Collections.shuffle(legalBoards);
            Collections.sort(legalBoards);

            int adjustedMaxBreadth;
            if (maxBreadth < legalBoards.size() && maxBreadth != -1) { //is maximum breadth smaller than number of legal moves?
                adjustedMaxBreadth = maxBreadth;
            } else {
                adjustedMaxBreadth = legalBoards.size();
            }

            for (int i = 0; i < adjustedMaxBreadth; i++) {
                Board legalBoard = legalBoards.get(i);
                Node childNode = new Node(legalBoard, this, maxDepth, maxBreadth, color);
                childNode.addNodes(depth + 1);
                this.addChild(childNode);
            }
        }
        setCascadedPieceValue();
    }
}
