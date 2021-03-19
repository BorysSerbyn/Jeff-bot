package ca.borysserbyn.jeffbot;

import ca.borysserbyn.mechanics.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Node implements Comparable{

    private int maxDepth;
    private int maxBreadth;
    private Move move;
    private Color jeffColor;
    private Color opponentColor;
    private Color turnColor;
    private int turnCount;
    private ArrayList<Node> childNodes;
    private Node parentNode;
    private float cascadedScore = 1000;
    private float currentScore = 0;
    private float pieceValue = 0;
    private int checkmateValue = 0;
    private int stalemateValue = 0;

    public Node(int maxDepth, Color jeffColor, Move move, int turnCount) {
        this.turnCount = turnCount;
        this.jeffColor = jeffColor;
        this.move = move;
        this.maxDepth = maxDepth;
        childNodes = new ArrayList<>();
        turnColor = turnCount%2 == 0 ? Color.WHITE : Color.BLACK;
        opponentColor = jeffColor == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    @Override
    public String toString() {
        return "Node{" +
                "turnCount=" + turnCount +
                ", move=" + move +
                ", cascaded score=" + cascadedScore +
                ", current score=" + currentScore +
                '}';
    }

    /**
     * compares two nodes
     * @param o represents the "other node"
     * @return -1 : "this" is worse than other node
     * @return 0 : "this" is equal to other node
     * @return 1 : "this" is better than other node
     */
    @Override
    public int compareTo(Object o) {

        Node otherNode = (Node) o;

        int minMaxValue = jeffColor == move.getPiece().getColor() ? 1 : -1;
        if(otherNode.cascadedScore > cascadedScore){
            return minMaxValue;
        }else if(otherNode.cascadedScore < cascadedScore){
            return -minMaxValue;
        }else{
            return 0;
        }
    }

    public Move getMove() {
        return move;
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

    public void setCascadedScore(float cascadedScore) {
        this.cascadedScore = cascadedScore;
    }

    public ArrayList<Node> getChildNodes() {
        return childNodes;
    }

    public void addChild(Node childNode) {
        childNodes.add(childNode);
    }

    public boolean isPromoting(Game game) {
        return game.getState() == GameState.PROMOTING_AND_EATING || game.getState() == GameState.PROMOTING_PAWN;
    }

    /**
     * Score based on:
     * 1. game outcome determined at the termination of addNodes method
     * 2. other heuristics defined in Board
     */

    public void scoreNode(Game game) {
        this.currentScore = game.scorePosition(jeffColor);
    }

    //inherits min or max child cascadedScore depending on board turn
    public void inheritChildScore() {
        if (this.childNodes.isEmpty()) {
            return;
        }
        cascadedScore = childNodes.get(0).cascadedScore;
        for (Node childNode : childNodes) {
            if(jeffColor == turnColor){
                cascadedScore = Math.max(childNode.cascadedScore, cascadedScore);
            }else{
                cascadedScore = Math.min(childNode.cascadedScore, cascadedScore);
            }
            //cascadedScore = childNode.cascadedScore * childNode.valueSign > cascadedScore * childNode.valueSign ? cascadedScore : childNode.cascadedScore;
        }
    }

    public int addNodes(int depth, Game game) {
        if (depth >= maxDepth || game.isGameOver()) {//is game over or desired depth reached?
            int moveCount = 0;
            if (game.getState() == GameState.CHECKMATE) {
                this.checkmateValue = jeffColor == move.getPiece().getColor() ? 1 : -1;
            } else if (game.getState() == GameState.STALEMATE) {
                stalemateValue = 1;
            }else{
                moveCount = 1;
            }
            scoreNode(game);
            cascadedScore = currentScore;
            return moveCount;
        }
        scoreNode(game);

        int positionsFound = 0;
        //tree traversal
        if (!this.getChildNodes().isEmpty()) {//does this node already have children?
            for (Node childNode : this.getChildNodes()) {
                Game clonedGame = (Game) game.clone();
                Move clonedMove = clonedGame.getMoveByClone(childNode.move);
                clonedGame.movePiece(clonedMove);
                positionsFound += childNode.addNodes(depth + 1, clonedGame);
            }
        } else {
            ArrayList<Move> allMovesList = game.generateLegalMovesByColor(game.getTurn());

            for (Move possibleMove : allMovesList) {
                Game clonedGame = (Game) game.clone();
                Move clonedMove = clonedGame.getMoveByClone(possibleMove);
                clonedGame.movePiece(clonedMove);

                if (possibleMove.getPromotionSnapShot() != PieceName.UNDEFINED) {
                    clonedGame.promotePawn(possibleMove);
                }

                Node childNode = new Node(maxDepth, jeffColor, possibleMove, clonedGame.getTurnCounter());
                childNode.parentNode = this;
                this.addChild(childNode);
                positionsFound += childNode.addNodes(depth + 1, clonedGame);
            }
        }
        if(depth == 1){
            //System.out.println(move.toSFNotation() + ": " + positionsFound);
        }
        inheritChildScore();
        return positionsFound;
    }

    public float[] testMinimax(int depth, Game game, float alpha, float beta){
        if (depth >= maxDepth || game.isGameOver()) {//is game over or desired depth reached?
            float moveCount = 0.f;
            if (game.getState() == GameState.CHECKMATE) {
                this.checkmateValue = jeffColor == move.getPiece().getColor() ? 1 : -1;
            } else if (game.getState() == GameState.STALEMATE) {
                stalemateValue = 1;
            }else{
                moveCount = 1;
            }
            scoreNode(game);
            cascadedScore = currentScore;
            return new float[]{cascadedScore, moveCount};
        }

        int positionsFound = 0;

        ArrayList<Move> allMovesList = game.generateLegalMovesByColor(game.getTurn());
        MoveComparator moveComparator = new MoveComparator(game);
        allMovesList.sort(moveComparator);

        float maxChildScore = -10000;
        float minChildScore = 10000;
        for (Move possibleMove : allMovesList) {
            Game clonedGame = (Game) game.clone();
            Move clonedMove = clonedGame.getMoveByClone(possibleMove);
            clonedGame.movePiece(clonedMove);

            Node childNode = new Node(maxDepth, jeffColor, possibleMove, clonedGame.getTurnCounter());
            childNode.parentNode = this;
            this.addChild(childNode);
            float[] childResult = childNode.testMinimax(depth + 1, clonedGame, alpha, beta);
            float childScore = childResult[0];
            positionsFound += childResult[1];

            if(turnColor == jeffColor){
                maxChildScore = Math.max(maxChildScore, childScore);
                alpha = Math.max(alpha, childScore);
                cascadedScore = maxChildScore;
                if(beta <= alpha){
                    break;
                }
            }else{
                minChildScore = Math.min(minChildScore, childScore);
                beta = Math.min(beta, childScore);
                cascadedScore = minChildScore;
                if(beta <= alpha){
                    break;
                }
            }
        }
        return new float[]{cascadedScore, positionsFound};
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            Game game = new Game(1);
            Node node = new Node(i, Color.WHITE, null, game.getTurnCounter());
            long start_time = System.nanoTime();
            int positionsFound = node.addNodes(0, game);
            long end_time = System.nanoTime();
            System.out.println("Depth: " + i + " Result: " + positionsFound + " Time: " + (end_time - start_time) / 1e6);
        }
    }
}
