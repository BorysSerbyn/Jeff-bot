package ca.borysserbyn.jeffbot;

import ca.borysserbyn.mechanics.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Node implements Comparable {
    private int maxDepth;
    private int maxBreadth;
    private Color color;
    private Color opponentColor;
    private Game game;
    private float cascadedScore;
    private float currentScore;
    private float pieceValue;
    private int checkmateValue;
    private int stalemateValue;
    private int valueSign;
    private ArrayList<Node> childNodes;
    private Node parentNode;


    public Node(Game game, Node parentNode, int maxDepth, int maxBreadth, Color color) {
        this.color = color;
        this.maxBreadth = maxBreadth;
        this.maxDepth = maxDepth;
        this.game = game;
        this.parentNode = parentNode;
        currentScore = 0;
        cascadedScore = 0;
        pieceValue = 0;
        checkmateValue = 0;
        stalemateValue = 0;
        childNodes = new ArrayList<>();
        opponentColor = color == Color.WHITE ? Color.BLACK : Color.WHITE;
        valueSign = game.getTurn() == color ? -1 : 1;
    }

    @Override
    public String toString() {
        return "Node{" +
                "turnCount=" + game.getTurnCounter() +
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

    public Game getGame() {
        return game;
    }

    public Move getLastMove() {
        return game.getLastMove();
    }

    public ArrayList<Node> getChildNodes() {
        return childNodes;
    }

    public void addChild(Node childNode) {
        childNodes.add(childNode);
    }

    /**
     * Score based on:
     * 1. game outcome determined at the termination of addNodes method
     * 2. other heuristics defined in Board
     */
    public void scoreNode() {
        float castlingValue = game.castlingValue(color) - game.castlingValue(opponentColor);
        pieceValue = game.getGameValueByColor(color);
        float score = pieceValue + checkmateValue * 20 + castlingValue/12;
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
            if(valueSign < 0){
                if(cascadedScore < childNode.cascadedScore){
                    cascadedScore = childNode.cascadedScore;
                }
            }else{
                if(cascadedScore > childNode.cascadedScore){
                    cascadedScore = childNode.cascadedScore;
                }
            }
            //cascadedScore = childNode.cascadedScore * childNode.valueSign > cascadedScore * childNode.valueSign ? cascadedScore : childNode.cascadedScore;
        }
    }


    public void addNodesTest(int depth, Game game){
        if(depth >= maxDepth){
            return;
        }

        ArrayList<Game> legalGames = game.generateLegalGamesByColor(game.getTurn());
        for (int i = 0; i < maxDepth; i++) {
            Game legalGame = legalGames.get(i);
            //promotes pawn to queen every time
            if (legalGame.getState() == GameState.PROMOTING_AND_EATING || legalGame.getState() == GameState.PROMOTING_PAWN) {//can the board promote a pawn?
                legalGame.promotePawn(legalGame.getPieceByClone(legalGame.getLastMove().moveToPiece()), PieceName.QUEEN);
            }
            Node childNode = new Node(legalGame, this, maxDepth, maxBreadth, color);
            childNode.addNodes(depth + 1, maxDepth, false);
            this.addChild(childNode);
        }

    }

    //recurive method that adds children to root node given certain specifications;
    public void addNodes(int depth, int adjustedMaxDepth, boolean secondTry) {
        if (depth >= adjustedMaxDepth || game.isGameOver()) {//is game over or desired depth reached?
            if (game.getState() == GameState.CHECKMATE) {
                this.checkmateValue = valueSign;
            } else if (game.getState() == GameState.STALEMATE) {
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
            float adjustedScore = childNodes.isEmpty() ? currentScore : cascadedScore;
            float filter = childNodes.isEmpty() ? 0 : 0;
            for (Node siblingNode : parentNode.getChildNodes()) {
                if(siblingNode.childNodes.isEmpty()){
                    continue;
                }
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
            ArrayList<Game> legalGames = game.generateLegalGamesByColor(game.getTurn());
            if (maxBreadth != -1) { //dont need to do this if were picking all boards
                Collections.shuffle(legalGames, new Random(game.getSeed()));
                Collections.sort(legalGames);
            }
            //reverses legal boards at layer 0 if the last pass wasnt successful
            if (secondTry && depth == 0) {
                Collections.reverse(legalGames);
            }
            //adjust max depth and max depth based on number of available moves
            int adjustedMaxBreadth = maxBreadth < legalGames.size() && maxBreadth != -1 ? maxBreadth : legalGames.size();
            for (int i = 0; i < adjustedMaxBreadth; i++) {
                Game legalGame = legalGames.get(i);
                //promotes pawn to queen every time
                if (legalGame.getState() == GameState.PROMOTING_AND_EATING || legalGame.getState() == GameState.PROMOTING_PAWN) {//can the board promote a pawn?
                    legalGame.promotePawn(legalGame.getPieceByClone(legalGame.getLastMove().moveToPiece()), PieceName.QUEEN);
                }
                Node childNode = new Node(legalGame, this, maxDepth, maxBreadth, color);
                childNode.addNodes(depth + 1, adjustedMaxDepth, false);
                this.addChild(childNode);
            }
        }
        inheritChildScore();
    }
}
