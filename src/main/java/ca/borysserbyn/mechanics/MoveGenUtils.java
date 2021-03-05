package ca.borysserbyn.mechanics;

import java.util.ArrayList;

public abstract class MoveGenUtils {

    public static ArrayList generateSlidingMoves(Piece[][] board, Piece piece, int[][] directionArray){
        ArrayList<Move> moveList = new ArrayList<>();
        for(int[] direction: directionArray){
            int tempX = piece.getX();
            int tempY = piece.getY();
            while(true){
                tempX += direction[0];
                tempY += direction[1];
                if(isOutOfBounds(tempX, tempY) || board[tempX][tempY] != null){
                    break;
                }
                moveList.add(new Move(piece, tempX, tempY));
            }
        }
        return moveList;
    }

    public static ArrayList generateRookMoves(Piece[][] board, Piece piece){
        int[][] directionArray = new int[][]{{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        return generateSlidingMoves(board, piece, directionArray);
    }

    public static ArrayList generateBiShopMoves(Piece[][] board, Piece piece){
        int[][] directionArray = new int[][]{{1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
        return generateSlidingMoves(board, piece, directionArray);
    }

    public static ArrayList generateQueenMoves(Piece[][] board, Piece piece){
        int[][] directionArray = new int[][]{{1, 1}, {-1, -1}, {-1, 1}, {1, -1}, {0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        return generateSlidingMoves(board, piece, directionArray);
    }




    public static boolean isOutOfBounds(int x, int y){
        return x < 0 || x > 7 || y < 0 || y > 7;
    }
}
