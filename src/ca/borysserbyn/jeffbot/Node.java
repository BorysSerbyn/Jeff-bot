package ca.borysserbyn.jeffbot;

import ca.borysserbyn.*;

import java.awt.*;
import java.util.ArrayList;

public class Node implements Comparable{
    private int maxDepth;
    private int maxBreadth;
    private static final Color SIDE = Color.WHITE;
    private int turnCount;
    Piece movedPiece;
    double pieceValue;
    double checkmateProb;
    double stalemateProb;
    ArrayList<Node> childrenNodes;
    Node parentNode;

    public Node(int turnCount, Piece movedPiece, Node parentNode, int maxDepth, int maxBreadth) {
        this.maxBreadth = maxBreadth;
        this.maxDepth = maxDepth;
        this.turnCount = turnCount;
        this.movedPiece = movedPiece;
        this.parentNode = parentNode;
        pieceValue = 0;
        checkmateProb = 0;
        stalemateProb = 0;
        childrenNodes = new ArrayList<>();
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

    public void addChild(Node childNode) {
        childrenNodes.add(childNode);
    }

    //recusively populates board tree with outcomes
    public double[] addNodes(Board board) {
        boolean isGameOver = board.isGameOver();
        if (isGameOver || turnCount >= maxDepth) {
            pieceValue = board.getBoardValueByColor(SIDE);
            if (board.getState() == BoardState.CHECKMATE) {
                checkmateProb = 1;
            } else if (board.getState() == BoardState.STALEMATE) {
                stalemateProb = 1;
            }
            return new double[]{pieceValue, checkmateProb, stalemateProb};
        }

        /*
        if (turnCount >= maxDepth) {
            return new double[]{pieceValue, checkmateProb, stalemateProb};
        }
        */
        int childCount = 0;
        double[] weightedOutcome = new double[3];
        Color color = turnCount % 2 == 0 ? Color.WHITE : Color.BLACK;
        for (Piece piece : board.getLegalMovesByColor(color)) {
            Node childNode = new Node(turnCount + 1, piece, parentNode, maxDepth, maxBreadth);
            this.addChild(childNode);
            Board clonedBoard = (Board) board.clone();
            Piece clonedPiece = clonedBoard.getPieceByName(piece.getPieceName(), piece.getColor());
            Tile clonedTile = clonedBoard.getTileByPosition(piece.getTile().getX(), piece.getTile().getY());
            board.movePiece(clonedPiece, clonedTile);
            Piece deadPiece = clonedBoard.getPieceByTile(clonedBoard.getGraveyard());
            double[] gameOutcome = childNode.addNodes(clonedBoard);
            for (int i = 0; i < 3; i++) {
                weightedOutcome[i] += gameOutcome[i];
            }
            childCount++;
            if(maxBreadth != -1 &&  childCount >= maxBreadth){
                break;
            }
        }
        for (int i = 0; i < 3; i++) {
            weightedOutcome[i] = weightedOutcome[i] / childCount;
        }
        return weightedOutcome;
    }
}
