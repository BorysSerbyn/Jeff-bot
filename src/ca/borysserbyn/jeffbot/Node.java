package ca.borysserbyn.jeffbot;

import ca.borysserbyn.*;

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
    private float pieceValue;
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

    public float getCascadedScore() {
        return cascadedScore;
    }

    public float getCurrentScore() {
        return currentScore;
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

    public float getPieceValue() {
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
        float score = pieceValue + checkmateValue * 20;
        //substract stalemate value if you are winning, do nothing if you are losing.
        score -= score > 0 ? stalemateValue * 20 : 0;
        this.currentScore = score;
    }

    //inherits min or max child cascadedScore depending on board turn
    public void inheritChildScore() {
        if (this.childNodes.isEmpty()) {
            return;
        }
        cascadedScore = childNodes.get(0).cascadedScore;
        for (Node childNode : childNodes) {
            cascadedScore = childNode.cascadedScore * childNode.valueSign > cascadedScore * childNode.valueSign ? childNode.cascadedScore : cascadedScore;
        }
    }

    //recurive method that adds children to root node given certain specifications;
    public void addNodes(int depth, int adjustedMaxDepth, boolean secondTry) {
        if (depth >= adjustedMaxDepth || board.isGameOver()) {//is game over or desired depth reached?
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

        /*
         * Alpha-Beta pruner
         * 1. prunes branch if a better alternative has already been calculated
         * 2. prunes branches where score has been bad for 2 consecutive turns
         */
        if (depth > 1) {//reliable pruning should start after layer 2 so that it doesnt prune useful branches
            //if this node already has children, use the cascaded score instead of the current one.
            float adjustedScore = this.childNodes.isEmpty() ? currentScore : cascadedScore;
            float filter = this.childNodes.isEmpty() ? 0 : 0;
            for (Node siblingNode : parentNode.getChildNodes()) {
                double siblingCascadedScore = siblingNode.cascadedScore;
                //add the value to the adjusted score to keep investigating small losses.
                if (siblingCascadedScore * valueSign > adjustedScore * valueSign + filter) {//is the current score worse than a siblings
                    cascadedScore = adjustedScore;
                    return;
                }
            }
        }
        //tree traversal
        if (!this.getChildNodes().isEmpty()) {//does this node already have children?
            for (Node childNode : this.getChildNodes()) {
                childNode.addNodes(depth + 1, adjustedMaxDepth, false);
            }
        } else {
            //aranges the legal boards so that the most relevant ones are used in the tree
            ArrayList<Board> legalBoards = board.getLegalBoardsByColor(board.getTurn());
            if(maxBreadth != -1){ //dont need to do this if were picking all boards
                Collections.shuffle(legalBoards);
                Collections.sort(legalBoards);
            }
            //reverses legal boards at layer 0 if the last pass wasnt successful
            if(secondTry && depth == 0){
                Collections.reverse(legalBoards);
            }
            //adjust max depth and max depth based on number of available moves
            int adjustedMaxBreadth = maxBreadth < legalBoards.size() && maxBreadth != -1 ? maxBreadth : legalBoards.size();
            for (int i = 0; i < adjustedMaxBreadth; i++) {
                Board legalBoard = legalBoards.get(i);
                //promotes pawn to queen every time
                if (legalBoard.getState() == BoardState.PROMOTING_AND_EATING || legalBoard.getState() == BoardState.PROMOTING_PAWN) {//can the board promote a pawn?
                    legalBoard.promotePawn(legalBoard.getPieceByClone(legalBoard.getLastMove().moveToPiece()), PieceName.QUEEN);
                }
                Node childNode = new Node(legalBoard, this, maxDepth, maxBreadth, color);
                childNode.addNodes(depth + 1, adjustedMaxDepth, false);
                this.addChild(childNode);
            }
        }
        inheritChildScore();
    }
}
