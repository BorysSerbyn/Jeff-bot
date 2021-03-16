package ca.borysserbyn.jeffbot;

import ca.borysserbyn.mechanics.*;

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
    private float cascadedScore = 0;
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
        float castlingValue = game.castlingValue(jeffColor) - game.castlingValue(opponentColor);
        pieceValue = game.getGameValueByColor(jeffColor);
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
            if(jeffColor == turnColor){
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


        if (depth > 2) {//reliable pruning should start after layer 2 so that it doesnt prune useful branches
            //if this node already has children, use the cascaded score instead of the current one.
            float adjustedScore = childNodes.isEmpty() ? currentScore : cascadedScore;
            float filter = 0f;
            for (Node siblingNode : parentNode.getChildNodes()) {
                if(siblingNode.childNodes.isEmpty()){
                    continue;
                }
                double siblingCascadedScore = siblingNode.cascadedScore;

                if(jeffColor != game.getTurn()){
                    if(siblingCascadedScore > adjustedScore){
                        cascadedScore = adjustedScore;
                        return 1;
                    }
                }else{
                    if(siblingCascadedScore < adjustedScore){
                        cascadedScore = adjustedScore;
                        return 1;
                    }
                }
                //add the value to the adjusted score to keep investigating small losses.
            }
        }

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

                if (isPromoting(clonedGame)) {
                    clonedGame.promotePawn(clonedMove.getPiece(), PieceName.QUEEN);
                }
                Node childNode = new Node(maxDepth, jeffColor, possibleMove, clonedGame.getTurnCounter());
                childNode.parentNode = this;
                positionsFound += childNode.addNodes(depth + 1, clonedGame);
                this.addChild(childNode);
            }
        }
        if(depth == 1){
            //System.out.println(move.toSFNotation() + ": " + positionsFound);
        }
        inheritChildScore();
        return positionsFound;
    }

    public String addNodes2(int depth, Game game) {
        if (depth >= maxDepth) {
            return move.toString();
        }
        ArrayList<Move> allMovesList = game.generateLegalMovesByColor(game.getTurn());
        String positionsFound = "";
        if (move != null) {
            positionsFound += move.toString()+ ":     ";
        }
        for (Move possibleMove : allMovesList) {
            Game clonedGame = (Game) game.clone();
            Move clonedMove = clonedGame.getMoveByClone(possibleMove);
            clonedGame.movePiece(clonedMove);

            if (isPromoting(clonedGame)) {
                clonedGame.promotePawn(clonedMove.getPiece(), PieceName.QUEEN);
            }
            Node childNode = new Node(maxDepth, jeffColor, possibleMove, clonedGame.getTurnCounter());

            positionsFound += childNode.addNodes2(depth + 1, clonedGame) + ", ";
            this.addChild(childNode);
        }
        positionsFound += "\n";
        return positionsFound;
    }

    public static void main(String[] args) {
//        Game game = FenUtils.createGameFromFen("rnbg1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
//        SimpleNode node = new SimpleNode(2, Color.WHITE, null);
//        String moves = node.addNodes2(0, game);
//        FileUtils.writeToFile(moves);

        //Game game = FenUtils.createGameFromFen("rnbg1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");

        /*for (int i = 1; i <= 4; i++) {
            Game game = FenUtils.createGameFromFen("rnQq1k1r/pp2bppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R b KQ - 1 8");
            SimpleNode node = new SimpleNode(i, Color.WHITE, null);
            long start_time = System.nanoTime();
            int positionsFound = node.addNodes(0, game);
            long end_time = System.nanoTime();
            System.out.println("Depth: " + i + " Result: " + positionsFound + " Time: " + (end_time - start_time) / 1e6);
        }*/

        //GameGUI.createJFrame(TestPanel.getSingletonInstance());

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
