package ca.borysserbyn.jeffbot;

import ca.borysserbyn.gui.ChessPanel;
import ca.borysserbyn.mechanics.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

public class Jeffbot {
    private static int maxDepth = 4;
    private static final ForkJoinPool pool = new ForkJoinPool();
    private Game game;
    private Color color;
    private Node currentNode;

    public Jeffbot(Color color, Game game) {
        this.color = color;
        this.game = (Game) game.clone();
        currentNode = new Node(maxDepth, color, null, game.getTurnCounter());
        buildTree(currentNode, false);
    }

    public void setBoard(Game game) {
        this.game = game;
        resetCurrentNode();
    }

    public Game getGame() {
        return game;
    }

    public Node getNodeByMove(Node node, Move move) {
        return node.getChildNodes().stream()
                .filter(child -> child.getMove().equals(move))
                .findFirst()
                .orElse(null);
    }

    public void resetCurrentNode() {
        currentNode = new Node(maxDepth, color, null, game.getTurnCounter());
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public Move findBestMove() {
        Collections.sort(currentNode.getChildNodes());
        Node bestMoveNode = currentNode.getChildNodes().get(0);
        printThoughtProcess(bestMoveNode);
        return bestMoveNode.getMove();
    }

    public void printThoughtProcess(Node bestMoveNode) {
        Node bestNode = bestMoveNode;
        while (true) {
            ArrayList<Node> siblings = bestNode.getParentNode().getChildNodes();
            Collections.sort(siblings);
            for (Node child : siblings) {
                System.out.print(child.getCascadedScore() + ", ");
            }
            System.out.println();
            System.out.println(bestNode);
            System.out.println();
            if (bestNode.getChildNodes().isEmpty()) {
                break;
            }
            Collections.sort(bestNode.getChildNodes());
            bestNode = bestNode.getChildNodes().get(0);
        }
    }

    public void secondTry() {
        System.out.println("Reseting the tree, no good moves found");
        resetCurrentNode();
        buildTree(currentNode, true);
        Collections.sort(currentNode.getChildNodes());
    }

    public void updateTree(Move move) {
        Move clonedMove = (Move) move.clone();
        Move moveJeffBoard = game.getMoveByClone(move);

        game.movePiece(moveJeffBoard);
        Node moveNode = getNodeByMove(currentNode, clonedMove);

        if (moveNode == null) {//is there a node in the tree corresponding the the move?
            System.out.println("Couldnt find node: " + move + " in tree.");
            resetCurrentNode();
        }else{
            currentNode = moveNode;
            //System.out.println("current node: " + currentNode + " move: " + move);
        }
        buildTree(currentNode, false);
    }


    //Handles bot movement.
    public void movePiece(ChessPanel chessPanel) {
        Game guiGame = chessPanel.getGame();

        Move bestMoveNodeBoard = findBestMove();
        Move bestMoveGUIBoard = guiGame.getMoveByClone(bestMoveNodeBoard);
        Move bestMoveJeffBoard = game.getMoveByClone(bestMoveNodeBoard);
        guiGame.movePiece(bestMoveGUIBoard);

        if (guiGame.getState() == GameState.PROMOTING_AND_EATING || guiGame.getState() == GameState.PROMOTING_PAWN) {
            guiGame.promotePawn(bestMoveGUIBoard.getPiece(), PieceName.QUEEN);
            getGame().promotePawn(bestMoveJeffBoard.getPiece(), PieceName.QUEEN);
            bestMoveNodeBoard.getPiece().setPieceName(PieceName.QUEEN);
        }

        chessPanel.initializePieces();
        chessPanel.endGameMessage();
        updateTree(bestMoveNodeBoard);
    }

    public void buildTree(Node node, boolean secondTry) {

        int treeSize = node.addNodes(0, (Game) game.clone());
        System.out.println("Tree built with size: " + treeSize);

        /*TreeTask rootTask = new TreeTask(node, 0, secondTry);
        pool.invoke(rootTask);
        node.getChildNodes().forEach(Node::inheritChildScore);*/

        node.setParentNode(null);
        System.gc();
    }
}
