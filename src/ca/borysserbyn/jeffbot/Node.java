package ca.borysserbyn.jeffbot;

import ca.borysserbyn.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Node implements Comparable {
    private int maxDepth;
    private int maxBreadth;
    private Color color;
    private Color opponentColor;
    private Board board;
    private float cascadedScore;
    private float currentScore;
    private int pieceValue;
    private int checkmateValue;
    private int stalemateValue;
    private int valueSign;
    private ArrayList<Node> childNodes;
    private Node parentNode;


    public Node(Board board, Node parentNode, int maxDepth, int maxBreadth, Color color) {
        this.color = color;
        this.maxBreadth = maxBreadth;
        this.maxDepth = maxDepth;
        this.board = board;
        this.parentNode = parentNode;
        currentScore = 0;
        cascadedScore = 0;
        pieceValue = 0;
        checkmateValue = 0;
        stalemateValue = 0;
        childNodes = new ArrayList<>();
        opponentColor = color == Color.WHITE ? Color.BLACK : Color.WHITE;
        valueSign = board.getTurn() == color ? -1 : 1;
    }

    @Override
    public String toString() {
        return "Node{" +
                "turnCount=" + board.getTurnCounter() +
                ", move=" + getLastMove().toString() +
                ", cascaded score=" + cascadedScore +
                ", current score=" + currentScore +
                '}';
    }

    //not final at all
    @Override
    public int compareTo(Object o) {
        Node node = (Node) o;
        double scoreDifference = (node.cascadedScore - this.cascadedScore) * valueSign;
        return (int) Math.signum(scoreDifference);
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

    public int getPieceValue() {
        return pieceValue;
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

    /**
     * Score based on:
     * 1. game outcome determined at the termination of addNodes method
     * 2. other heuristics defined in Board
     */
    public void scoreNode() {
        pieceValue = board.getBoardValueByColor(color);

        float centerPawnValue = Math.signum(board.centerPawnValue(color) - board.centerPawnValue(opponentColor));
        float kingProtectionValue = Math.signum(board.kingProtectionValue(color) - board.kingProtectionValue(opponentColor));
        float queenProtectionValue = Math.signum(board.queenProtectionValue(color) - board.queenProtectionValue(opponentColor));
        float centerKnightValue = Math.signum(board.centerKnightValue(color) - board.centerKnightValue(opponentColor));

        float score = pieceValue +
                checkmateValue * 20 +
                centerPawnValue / 4 +
                kingProtectionValue/4+
                centerKnightValue/5+
                queenProtectionValue/8;
        //substract stalemate value if you are winning, do nothing if you are losing.
        score -= score > 0 ? stalemateValue * 20 : 0;
        this.currentScore = score;
    }


    //inherits min or max child cascadedScore depending on board turn
    public void inheritBestChildScore() {
        if (this.childNodes.isEmpty()) {
            return;
        }
        cascadedScore = childNodes.get(0).cascadedScore;
        for (Node childNode : childNodes) {
            cascadedScore = childNode.cascadedScore * childNode.valueSign > cascadedScore * childNode.valueSign ? childNode.cascadedScore : cascadedScore;
        }
    }

    //recurive method that adds children to root node given certain specifications;
    public void addNodes(int depth, int adjustedMaxDepth) {
        if (board.isGameOver() || depth >= adjustedMaxDepth) {//is game over or desired depth reached?
            if (board.getState() == BoardState.CHECKMATE) {
                this.checkmateValue = valueSign;
            } else if (board.getState() == BoardState.STALEMATE) {
                stalemateValue = 1;
            }
            scoreNode();
            cascadedScore = currentScore;
            return;
        }
        scoreNode();

        /**
         * Alpha-Beta pruner
         * prunes branch if a better alternative has already been calculated
         */
        if (depth > 1) {//single threading starts at depth of 2, which means we can take cascading score into account.
            //if this node already has children, use the cascaded score instead of the current one.
            float adjustedScore = this.childNodes.isEmpty() ? currentScore : cascadedScore;
            for (Node siblingNode : parentNode.getChildNodes()) {
                double siblingCascadedScore = siblingNode.cascadedScore;
                //add the value to the adjusted score to keep investigating small losses.
                if (siblingCascadedScore * valueSign > adjustedScore * valueSign) {//is the current score worse than a siblings
                    cascadedScore = adjustedScore;
                    return;
                }
            }
        }
        if (!this.getChildNodes().isEmpty()) {//does this node already have children?
            for (Node childNode : this.getChildNodes()) {
                childNode.addNodes(depth + 1, adjustedMaxDepth);
            }
        } else {
            ArrayList<Board> legalBoards = board.getLegalBoardsByColor(board.getTurn());
            Collections.shuffle(legalBoards);
            Collections.sort(legalBoards);

            //adjust max breadth to the number of available moves
            int adjustedMaxBreadth = maxBreadth < legalBoards.size() && maxBreadth != -1 ? maxBreadth : legalBoards.size();

            for (int i = 0; i < adjustedMaxBreadth; i++) {
                //pick from neutral board position when retrying
                Board legalBoard = legalBoards.get(i);
                //promotes pawn to queen every time
                if (legalBoard.getState() == BoardState.PROMOTING_AND_EATING || legalBoard.getState() == BoardState.PROMOTING_PAWN) {//can the board promote a pawn?
                    legalBoard.promotePawn(legalBoard.getPieceByClone(legalBoard.getLastMove().moveToPiece()), PieceName.QUEEN);
                }
                Node childNode = new Node(legalBoard, this, maxDepth, maxBreadth, color);
                childNode.addNodes(depth + 1, adjustedMaxDepth);
                this.addChild(childNode);
            }
        }
        inheritBestChildScore();
    }
}
