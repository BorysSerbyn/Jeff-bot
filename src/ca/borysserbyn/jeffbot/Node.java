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
    private int maxRetries;

    private double checkmateProb;
    private double stalemateProb;
    private ArrayList<Node> childNodes;
    private Node parentNode;


    public Node(Board board, Node parentNode, int maxDepth, int maxBreadth, Color color, int maxRetries) {
        this.color = color;
        this.maxBreadth = maxBreadth;
        this.maxDepth = maxDepth;
        this.board = board;
        this.parentNode = parentNode;
        cascadedPieceValue = 0;
        currentPieceValue = 0;
        this.maxRetries = maxRetries;
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
        double totalIncrease = nodeToCompare.cascadedPieceValue - this.cascadedPieceValue;
        if(checkmateProb == nodeToCompare.checkmateProb){
            return (int) totalIncrease;
        }else if(checkmateProb > nodeToCompare.checkmateProb){
            return -1;
        }else{
            return 1;
        }
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

    public void cascadeGameOutcome(){
        cascadedPieceValue = childNodes.get(0).getCascadedPieceValue();
        checkmateProb = childNodes.get(0).checkmateProb;
        stalemateProb = childNodes.get(0).stalemateProb;
        for (Node childNode : childNodes) {
            double childPieceValue = childNode.getCascadedPieceValue();
            double childCheckmateProb = childNode.checkmateProb;
            double childStalemateProb = childNode.stalemateProb;


            if(board.getTurn() == color){ //if its your turn; choose the best outcome
                cascadedPieceValue = childPieceValue > cascadedPieceValue ? childPieceValue : cascadedPieceValue;
                checkmateProb = childCheckmateProb > checkmateProb ? childCheckmateProb : checkmateProb;
                stalemateProb = childStalemateProb > stalemateProb ? childStalemateProb : stalemateProb;
            }else{// if not; choose the worst one
                cascadedPieceValue = childPieceValue < cascadedPieceValue ? childPieceValue : cascadedPieceValue;
                checkmateProb = childCheckmateProb < checkmateProb ? childCheckmateProb : checkmateProb;
                stalemateProb = childStalemateProb < stalemateProb ? childStalemateProb : stalemateProb;
            }
        }
    }

    //recusively populates tree with outcomes
    public void addNodes(int depth) {
        currentPieceValue = board.getBoardValueByColor(color);

        if(depth == 2){ //filter out branches with bad outcomes at depth 2 to optimize computing time.
            double rootPieceValue = parentNode.parentNode.getCurrentPieceValue();
            if(currentPieceValue+1 < rootPieceValue){
                cascadedPieceValue = currentPieceValue;
                if (board.getState() == BoardState.CHECKMATE) {
                    checkmateProb = board.getTurn() == color ? -1 : 1;
                } else if (board.getState() == BoardState.STALEMATE) {
                    stalemateProb = 1;
                }
                return;
            }
        }

        if (board.isGameOver() || depth >= maxDepth) {//is game over or desired depth reached?
            cascadedPieceValue = currentPieceValue;
            if (board.getState() == BoardState.CHECKMATE) {
                checkmateProb = 1;
            } else if (board.getState() == BoardState.STALEMATE) {
                stalemateProb = 1;
            }
            return;
        }
        if (!this.getChildNodes().isEmpty()) {//does this node already have children?
            for (Node childNode : this.getChildNodes()) {
                childNode.addNodes(depth + 1);
            }
        } else {
            ArrayList<Board> legalBoards = board.getLegalBoardsByColor(board.getTurn());
            Collections.shuffle(legalBoards);
            Collections.sort(legalBoards);

            int adjustedMaxRetries = maxRetries;
            int adjustedMaxBreadth;
            int numberOfRecalcs = 0;
            if (maxBreadth < legalBoards.size() && maxBreadth != -1) { //is maximum breadth smaller than number of legal moves?
                adjustedMaxBreadth = maxBreadth;
            } else {
                adjustedMaxBreadth = legalBoards.size();
            }

            if(depth == 0){ // only choose 1 branch at a time for starter move until good one is found.
                adjustedMaxBreadth = 1;
                adjustedMaxRetries += 5;
            }

            for (int i = 0; i < adjustedMaxBreadth + numberOfRecalcs; i++) {
                Board legalBoard = i < adjustedMaxBreadth ? legalBoards.get(i) : legalBoards.get(legalBoards.size() - numberOfRecalcs); //pick from neutral board position if eating doesnt seem to help
                Node childNode = new Node(legalBoard, this, maxDepth, maxBreadth, color, maxRetries);
                childNode.addNodes(depth + 1);
                this.addChild(childNode);

                boolean branchFails = (board.getTurn() == color ^ childNode.getCascadedPieceValue() > currentPieceValue) && childNode.getCascadedPieceValue() != currentPieceValue;

                if(branchFails && adjustedMaxRetries > numberOfRecalcs) { //if the outcome of the child is bad and there is still room for retries, try again.
                    numberOfRecalcs = adjustedMaxBreadth + numberOfRecalcs < legalBoards.size() ? numberOfRecalcs + 1 : numberOfRecalcs;
                }
            }
        }
        cascadeGameOutcome();
    }
}
