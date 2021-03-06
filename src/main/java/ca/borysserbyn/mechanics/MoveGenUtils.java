package ca.borysserbyn.mechanics;

import java.util.ArrayList;

public abstract class MoveGenUtils {

    public static final int[][] diagonalArray = new int[][]{{1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
    public static final int[][] lineArray = new int[][]{{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
    public static final int[][] slidingArray = new int[][]{{1, 1}, {-1, -1}, {-1, 1}, {1, -1}, {0, 1}, {0, -1}, {-1, 0}, {1, 0}};
    public static final int[][] knightArray = new int[][]{{1, 2}, {-1, 2}, {1, -2}, {-1, -2}, {2, 1}, {-2, 1}, {2, -1}, {-2, -1}};
    public static final int[][] simplePawnArray = new int[][]{{0, 1}, {0, 2}};
    public static final int[][] reverseSimplePawnArray = new int[][]{{0, -1}, {0, -2}};
    public static final int[][] eatingPawnArray = new int[][]{{1, 1}, {-1, 1}};
    public static final int[][] reverseEatingPawnArray = new int[][]{{-1, 1}, {-1, -1}};

    public static ArrayList generateSimpleMoves(Game game, Piece piece, int[][] directionArray, boolean limitedMobibilty) {

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
                Piece targetPiece = game.getBoard()[tempX][tempY];
                if (targetPiece != null && targetPiece.getColor() == piece.getColor()) { //is friendly piece on square
                    break;
                }
                if (isDangerous) {
                    Move currentMove = new Move(piece, tempX, tempY);
                    if (!game.willKingBeChecked(currentMove)) {
                        moveList.add(new Move(piece, tempX, tempY));
                    }
                } else {
                    moveList.add(new Move(piece, tempX, tempY));
                }
                if (limitedMobibilty) {
                    break;
                }
            }
        }
        return moveList;
    }

    public static ArrayList generateRookMoves(Game game, Piece piece) {
        return generateSimpleMoves(game, piece, lineArray, false);
    }

    public static ArrayList generateBiShopMoves(Game game, Piece piece) {
        return generateSimpleMoves(game, piece, diagonalArray, false);
    }

    public static ArrayList generateQueenMoves(Game game, Piece piece) {
        return generateSimpleMoves(game, piece, slidingArray, false);
    }

    public static ArrayList generateKnightMoves(Game game, Piece piece) {
        return generateSimpleMoves(game, piece, knightArray, true);
    }

    public static ArrayList generateKingMoves(Game game, Piece piece) {
        return generateSimpleMoves(game, piece, slidingArray, true);
    }

    public static ArrayList generatePawnMoves(Game game, Piece piece) {
        ArrayList<Move> moveList = new ArrayList<>();
        if(game.pawnOrientationByColor(piece.getColor()) == 1){
            moveList.addAll(generateSimpleMoves(game, piece, simplePawnArray, true));
            moveList.addAll(generatePawnEatingMoves(game, piece, eatingPawnArray));
            moveList.addAll(generateEnPassantMoves(game, piece, eatingPawnArray));

        }else{
            moveList.addAll(generateSimpleMoves(game, piece, reverseSimplePawnArray, true));
            moveList.addAll(generatePawnEatingMoves(game, piece, reverseEatingPawnArray));
            moveList.addAll(generateEnPassantMoves(game, piece, reverseEatingPawnArray));
        }
        return moveList;
    }

    public static ArrayList generateEnPassantMoves(Game game, Piece piece, int[][] directionArray) {
        Move discardMove = new Move(piece, -1, -1);
        boolean isDangerous = game.willKingBeChecked(discardMove);
        ArrayList<Move> moveList = new ArrayList<>();

        for (int[] transformation : directionArray) {
            int tempX = piece.getX() + transformation[0];
            int tempY = piece.getY() + transformation[1];
            if (isOutOfBounds(tempX, tempY)) {
                break;
            }
            Piece targetPiece = game.getBoard()[tempX][tempY];

            if (isDangerous) {
                Move currentMove = new Move(piece, tempX, tempY);
                if (!game.willKingBeChecked(currentMove)) {
                    moveList.add(new Move(piece, tempX, tempY));
                }
            } else {
                moveList.add(new Move(piece, tempX, tempY));
            }
        }
        return moveList;
    }

    public static ArrayList generatePawnEatingMoves(Game game, Piece piece, int[][] directionArray) {
        Move discardMove = new Move(piece, -1, -1);
        boolean isDangerous = game.willKingBeChecked(discardMove);
        ArrayList<Move> moveList = new ArrayList<>();

        for (int[] transformation : directionArray) {
            int tempX = piece.getX() + transformation[0];
            int tempY = piece.getY() + transformation[1];
            if (isOutOfBounds(tempX, tempY)) {
                break;
            }
            Piece targetPiece = game.getBoard()[tempX][tempY];
            if(targetPiece == null){
                continue;
            }
            if (targetPiece.getColor() == piece.getColor()) { //is friendly piece on square
                continue;
            }
            if (isDangerous) {
                Move currentMove = new Move(piece, tempX, tempY);
                if (!game.willKingBeChecked(currentMove)) {
                    moveList.add(new Move(piece, tempX, tempY));
                }
            } else {
                moveList.add(new Move(piece, tempX, tempY));
            }
        }
        return moveList;
    }


    public static boolean isOutOfBounds(int x, int y) {
        return x < 0 || x > 7 || y < 0 || y > 7;
    }
}
