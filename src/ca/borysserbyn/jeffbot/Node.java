package ca.borysserbyn.jeffbot;

import ca.borysserbyn.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

public class Node implements Comparable{
    private int maxDepth;
    private int maxBreadth;
    private  Color color = Color.WHITE;
    private int turnCount;
    private Move move;
    private double pieceValue;
    private double checkmateProb;
    private double stalemateProb;
    private ArrayList<Node> childrenNodes;
    private Node parentNode;


    public Node(int turnCount, Move move, Node parentNode, int maxDepth, int maxBreadth, Color color) {
        this.color = color;
        this.maxBreadth = maxBreadth;
        this.maxDepth = maxDepth;
        this.turnCount = turnCount;
        this.move = move;
        this.parentNode = parentNode;
        pieceValue = 0;
        checkmateProb = 0;
        stalemateProb = 0;
        childrenNodes = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Node{" +
                "turnCount=" + turnCount +
                ", move=" + move.toString() +
                '}';
    }

    //not final at all
    @Override
    public int compareTo(Object o) {
        Node nodeToCompare = (Node) o;
        double checkmateIncrease = (this.checkmateProb - nodeToCompare.checkmateProb)/1;
        double pieceValueIncrease = (this.pieceValue - nodeToCompare.pieceValue)/38;
        double totalIncrease = checkmateIncrease + pieceValueIncrease;
        return totalIncrease > 0 ? 0 : 1;
    }

    public Move getMove() {
        return move;
    }

    public double getPieceValue() {
        return pieceValue;
    }

    public double getCheckmateProb() {
        return checkmateProb;
    }

    public double getStalemateProb() {
        return stalemateProb;
    }

    public ArrayList<Node> getChildrenNodes(){return childrenNodes;}

    public void setPieceValue(double pieceValue) {
        this.pieceValue = pieceValue;
    }

    public void setCheckmateProb(double checkmateProb) {
        this.checkmateProb = checkmateProb;
    }

    public void setStalemateProb(double stalemateProb) {
        this.stalemateProb = stalemateProb;
    }

    public void addChild(Node childNode) {
        childrenNodes.add(childNode);
    }

    public void removeAllChildren(){
        childrenNodes = new ArrayList<Node>();
    }

    //recusively populates board tree with outcomes
    public double[] addNodes(Board board, int depth) {

        boolean isGameOver = board.isGameOver();
        if (isGameOver || depth > maxDepth) {//is game over or desired depth reached?
            pieceValue = board.getBoardValueByColor(color);
            if (board.getState() == BoardState.CHECKMATE) {
                checkmateProb = 1;
            } else if (board.getState() == BoardState.STALEMATE) {
                stalemateProb = 1;
            }
            return new double[]{pieceValue, checkmateProb, stalemateProb};
        }

        int childCount = 0;
        double[] weightedOutcome = new double[3];
        Color turnColor = turnCount % 2 == 0 ? Color.WHITE : Color.BLACK;

        if(!this.getChildrenNodes().isEmpty()){//does this node already have children?
            for (Node childNode : this.getChildrenNodes()) {
                Board clonedBoard = (Board) board.clone();
                double[] gameOutcome = childNode.addNodes(clonedBoard, depth + 1);
                for (int i = 0; i < 3; i++) {
                    weightedOutcome[i] += gameOutcome[i];
                }
            }
        }else{
            ArrayList<Move> allLegalMoves = board.getLegalMovesByColor(turnColor);
            //Collections.shuffle(allLegalMoves);
            int adjustedMaxBreadth = maxBreadth < allLegalMoves.size()  ? maxBreadth : allLegalMoves.size();

            if(maxBreadth == -1){
                adjustedMaxBreadth = allLegalMoves.size();
            }
            for (int i = 0; i < adjustedMaxBreadth; i++) {
                Move move = allLegalMoves.get(i);
                Move clonedMove = (Move) move.clone();
                Board clonedBoard = (Board) board.clone();
                Piece clonedPiece = clonedBoard.getPieceByClone(move.getPiece());
                Tile clonedTile = clonedBoard.getTileByClone(move.getTile());
                clonedBoard.movePiece(clonedPiece, clonedTile);

                Node childNode = new Node(turnCount + 1, clonedMove, parentNode, maxDepth, maxBreadth, color);
                this.addChild(childNode);

                double[] gameOutcome = childNode.addNodes(clonedBoard, depth + 1);
                for (int ii = 0; ii < 3; ii++) {
                    weightedOutcome[ii] += gameOutcome[ii];
                }
                childCount++;
            }
        }

        for (int i = 0; i < 3; i++) {
            weightedOutcome[i] = weightedOutcome[i] / childCount;
        }

        this.setPieceValue(weightedOutcome[0]);
        this.setCheckmateProb(weightedOutcome[1]);
        this.setStalemateProb(weightedOutcome[2]);
        return weightedOutcome;
    }
}
