package ca.borysserbyn.mechanics;

import ca.borysserbyn.gui.TestPanel;

import java.util.ArrayList;

public abstract class MoveGenUtils {

    public static final int[][] diagonalArray = new int[][]{{1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
    public static final int[][] lineArray = new int[][]{{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
    public static final int[][] slidingArray = new int[][]{{1, 1}, {-1, -1}, {-1, 1}, {1, -1}, {0, 1}, {0, -1}, {-1, 0}, {1, 0}};
    public static final int[][] knightArray = new int[][]{{1, 2}, {-1, 2}, {1, -2}, {-1, -2}, {2, 1}, {-2, 1}, {2, -1}, {-2, -1}};
    public static final int[][] simplePawnArray = new int[][]{{0, 1}, {0, 2}};
    public static final int[][] reverseSimplePawnArray = new int[][]{{0, -1}, {0, -2}};
    public static final int[][] eatingPawnArray = new int[][]{{1, 1}, {-1, 1}};
    public static final int[][] reverseEatingPawnArray = new int[][]{{1, -1}, {-1, -1}};
    public static final int[][] castlingArray = new int[][]{{2,0}, {-2, 0}};

    public static ArrayList<Move> generateRookMoves(Game game, Piece piece) {
        return generateSimpleMoves(game, piece, lineArray, false);
    }

    public static ArrayList<Move> generateBiShopMoves(Game game, Piece piece) {
        return generateSimpleMoves(game, piece, diagonalArray, false);
    }

    public static ArrayList<Move> generateQueenMoves(Game game, Piece piece) {
        return generateSimpleMoves(game, piece, slidingArray, false);
    }

    public static ArrayList<Move> generateKnightMoves(Game game, Piece piece) {
        return generateSimpleMoves(game, piece, knightArray, true);
    }

    public static ArrayList<Move> generateKingMoves(Game game, Piece piece) {
        ArrayList<Move> moveList = new ArrayList<>();
        moveList.addAll(generateSimpleMoves(game, piece, slidingArray, true));
        moveList.addAll(generateCastlingMoves(game, piece));
        return moveList;
    }

    public static ArrayList generatePawnMoves(Game game, Piece piece) {
        Move discardMove = new Move(piece, -1, -1);
        boolean isDangerous = game.willKingBeChecked(discardMove);
        ArrayList<Move> moveList = new ArrayList<>();

        if(game.pawnOrientationByColor(piece.getColor()) == 1){
            moveList.addAll(generateSimplePawnMoves(game, piece, simplePawnArray, isDangerous));
            moveList.addAll(generatePawnEatingMoves(game, piece, eatingPawnArray, isDangerous));
            moveList.addAll(generateEnPassantMoves(game, piece, eatingPawnArray));

        }else{
            moveList.addAll(generateSimplePawnMoves(game, piece, reverseSimplePawnArray, isDangerous));
            moveList.addAll(generatePawnEatingMoves(game, piece, reverseEatingPawnArray, isDangerous));
            moveList.addAll(generateEnPassantMoves(game, piece, reverseEatingPawnArray));
        }
        return moveList;
    }



    public static ArrayList<Move> generateSimpleMoves(Game game, Piece piece, int[][] directionArray, boolean limitedMobibilty) {
        Move discardMove = new Move(piece, -1, -1);
        boolean isDangerous;
        if (piece.getPieceName() == PieceName.KING) {
            isDangerous = true;
        } else {
            isDangerous = game.willKingBeChecked(discardMove);
        }

        ArrayList<Move> moveList = new ArrayList<>();

        for (int[] transformation : directionArray) {
            int tempX = piece.getX();
            int tempY = piece.getY();
            while (true) {
                tempX += transformation[0];
                tempY += transformation[1];

                if (isOutOfBounds(tempX, tempY)) {
                    break;
                }
                Piece targetPiece = game.getPieceByTile(tempX, tempY);
                if (targetPiece != null && targetPiece.getColor() == piece.getColor()) { //is friendly piece on square
                    break;
                }

                Move currentMove = new Move(piece, tempX, tempY);
                if (isDangerous) {
                    if (!game.willKingBeChecked(currentMove)) {
                        moveList.add(currentMove);
                    }
                } else {
                    moveList.add(currentMove);
                }
                if (limitedMobibilty) {
                    break;
                }
                if (targetPiece != null && targetPiece.getColor() != piece.getColor()) { //is enemy piece on square? then stop
                    break;
                }
            }
        }
        return moveList;
    }

    public static ArrayList<Move> generateSimplePawnMoves(Game game, Piece piece, int[][] directionArray, boolean isDangerous){
        ArrayList<Move> moveList = new ArrayList<>();

        for (int[] transformation : directionArray) {
            int tempX = piece.getX() + transformation[0];
            int tempY = piece.getY() + transformation[1];
            Move move = new Move(piece, tempX, tempY);

            if (isOutOfBounds(tempX, tempY)) {
                continue;
            }
            boolean pawnTest = game.isPawnMove1Legal(move);
            boolean moveCheck = Math.abs(transformation[1]) == 1 ? game.isPawnMove1Legal(move) : game.isPawnMove2Legal(move);

            /*if(Math.abs(transformation[1]) == 1) {
                TestPanel testPanel = TestPanel.getSingletonInstance();
                testPanel.setGame(game);
                testPanel.hilightTileRed(piece.getX(), piece.getY());
                testPanel.hilightTileBlue(tempX, tempY);
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    System.out.println("fuckie");
                }
            }*/

            if (moveCheck) {
                if (isDangerous) {
                    if (!game.willKingBeChecked(move)) {
                        moveList.add(move);
                    }
                } else {
                    moveList.add(move);
                }
            }
        }
        return moveList;
    }

    public static ArrayList<Move> generateEnPassantMoves(Game game, Piece piece, int[][] directionArray) {
        ArrayList<Move> moveList = new ArrayList<>();

        for (int[] transformation : directionArray) {
            int tempX = piece.getX() + transformation[0];
            int tempY = piece.getY() + transformation[1];
            Move move = new Move(piece, tempX, tempY);

            if (isOutOfBounds(tempX, tempY)) {
                continue;
            }
            if(game.isPawnEnPassantLegal(move)){
                if (!game.willKingBeChecked(move)) {
                    moveList.add(move);
                }
            }
        }
        return moveList;
    }

    public static ArrayList<Move> generatePawnEatingMoves(Game game, Piece piece, int[][] directionArray, boolean isDangerous) {
        ArrayList<Move> moveList = new ArrayList<>();

        for (int[] transformation : directionArray) {
            int tempX = piece.getX() + transformation[0];
            int tempY = piece.getY() + transformation[1];
            Move move = new Move(piece, tempX, tempY);

            if (isOutOfBounds(tempX, tempY)) {
                continue;
            }
            if(game.isPawnEatLegal(move)){
                if (isDangerous) {
                    if (!game.willKingBeChecked(move)) {
                        moveList.add(move);
                    }
                } else {
                    moveList.add(move);
                }
            }
        }
        return moveList;
    }

    public static ArrayList<Move> generateCastlingMoves(Game game, Piece piece){
        ArrayList<Move> moveList = new ArrayList<>();
        for (int[] transformation : castlingArray) {
            int tempX = piece.getX() + transformation[0];
            int tempY = piece.getY() + transformation[1];
            Move move = new Move(piece, tempX, tempY);

            if (isOutOfBounds(tempX, tempY)) {
                continue;
            }

            if (game.isCastlingLegal(move)) {
                if (!game.willKingBeChecked(move)) {
                    moveList.add(move);
                }
            }
        }
        return moveList;
    }


    public static boolean isOutOfBounds(int x, int y) {
        return x < 0 || x > 7 || y < 0 || y > 7;
    }
}
