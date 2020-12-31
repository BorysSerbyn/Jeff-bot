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
    private int currentPieceValue;
    private int cascadedPieceValue;
    private int maxRetries;

    private int checkmateProb;
    private int stalemateProb;
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
                ", value=" + scoreNode() +
                '}';
    }

    //not final at all
    @Override
    public int compareTo(Object o) {
        Node node = (Node) o;
        double scoreDifference = node.scoreNode() - this.scoreNode();
        return (int) scoreDifference;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getCurrentPieceValue() {
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

    public int getCascadedPieceValue() {
        return cascadedPieceValue;
    }

    public double scoreNode(){
        return cascadedPieceValue+checkmateProb*20;
    }

    public void inheritGameOutcome(){
        cascadedPieceValue = childNodes.get(0).getCascadedPieceValue();
        checkmateProb = childNodes.get(0).checkmateProb;
        stalemateProb = childNodes.get(0).stalemateProb;
        int valueSign = board.getTurn() == color ? 1 : -1;

        for (Node childNode : childNodes) {//find minmaxed outcome
            int childPieceValue = childNode.getCascadedPieceValue();
            int childCheckmateProb = childNode.checkmateProb;
            int childStalemateProb = childNode.stalemateProb;
            cascadedPieceValue = childPieceValue*valueSign > cascadedPieceValue*valueSign ? childPieceValue : cascadedPieceValue;
            checkmateProb = childCheckmateProb*valueSign > checkmateProb*valueSign ? valueSign : checkmateProb;
            stalemateProb = childStalemateProb > stalemateProb ? childStalemateProb : stalemateProb;
        }
    }


    //min-nax algorithm
    public void addNodes(int depth, int adjustedMaxDepth) {
        /**
         * Min Max values
         * the following variable definitions will make sure that each layer will properly alternate between minimizing and maximizing the value.
         */
        currentPieceValue = board.getBoardValueByColor(color);
        int valueSign = board.getTurn() == color ? -1 : 1;
        int signedCurrentPieceValue = currentPieceValue*valueSign;
        int checkmateValue = valueSign;
        int signedCascadedPieceValue = 0;
        if(!this.childNodes.isEmpty()){
            signedCascadedPieceValue = valueSign*cascadedPieceValue;
        }

        /**
         * Alpha-Beta pruner
         * prunes branch if a better alternative has already been calculated (compares cascaded piece value of sibling to currentpiece value)
         */
        if(depth > 1){//root node doesnt need to be pruned
            for (Node siblingNode : parentNode.getChildNodes()) {
                int siblingSignedCascadedPieceValue = signedCascadedPieceValue*valueSign;
                int adjustedPieceValue = this.childNodes.isEmpty() ? signedCurrentPieceValue : siblingSignedCascadedPieceValue;
                if(siblingSignedCascadedPieceValue > adjustedPieceValue+1){//prunes branches if better one was already found
                    cascadedPieceValue = currentPieceValue;
                    if (board.getState() == BoardState.CHECKMATE) {
                        checkmateProb = checkmateValue;
                    } else if (board.getState() == BoardState.STALEMATE) {
                        stalemateProb = 1;
                    }
                    return;
                }
            }
        }
        if (board.isGameOver() || depth >= adjustedMaxDepth) {//is game over or desired depth reached?
            cascadedPieceValue = currentPieceValue;
            if (board.getState() == BoardState.CHECKMATE) {
                checkmateProb = checkmateValue;
            } else if (board.getState() == BoardState.STALEMATE) {
                stalemateProb = 1;
            }
            return;
        }
        if (!this.getChildNodes().isEmpty()) {//does this node already have children?
            for (Node childNode : this.getChildNodes()) {
                childNode.addNodes(depth + 1, adjustedMaxDepth);
            }
        } else {
            ArrayList<Board> legalBoards = board.getLegalBoardsByColor(board.getTurn());
            Collections.shuffle(legalBoards);
            Collections.sort(legalBoards);

            int adjustedMaxRetries = maxRetries;
            int adjustedMaxBreadth;
            int numberOfRecalcs = 0;
            int childValueSign = valueSign * -1;

            if (maxBreadth < legalBoards.size() && maxBreadth != -1) { //is maximum breadth smaller than number of legal moves?
                adjustedMaxBreadth = maxBreadth;
            } else {
                adjustedMaxBreadth = legalBoards.size();
            }

            for (int i = 0; i < adjustedMaxBreadth + numberOfRecalcs; i++) {
                Board legalBoard = i < adjustedMaxBreadth ? legalBoards.get(i) : legalBoards.get(legalBoards.size() - numberOfRecalcs); //pick from neutral board position if eating doesnt seem to help
                if(legalBoard.getState() == BoardState.PROMOTING_AND_EATING || board.getState() == BoardState.PROMOTING_PAWN){//promote pawns
                    legalBoard.promotePawn(legalBoard.getPieceByClone(getLastMove().moveToPiece()), PieceName.QUEEN);
                }
                Node childNode = new Node(legalBoard, this, maxDepth, maxBreadth, color, maxRetries);
                childNode.addNodes(depth + 1, adjustedMaxDepth);
                this.addChild(childNode);
                int signedChildCascadedPieceValue = childNode.getCascadedPieceValue() *childValueSign;

                if(signedChildCascadedPieceValue+1 < signedCurrentPieceValue) { //does this move have a significant negative value compared to the current board position?
                    if(adjustedMaxBreadth + numberOfRecalcs < legalBoards.size()){ //are there enough possible moves to try again?
                        if(adjustedMaxRetries > numberOfRecalcs){ //have we already reached the maximum amount of retries?
                            numberOfRecalcs++;
                        }
                    }
                }
            }
        }
        inheritGameOutcome(); //cascade best or worst outcome from children
    }
}
