package ca.borysserbyn.mechanics;

import java.util.Locale;

public class BoardUtils {

    public static String createFenFromBoard(Piece[][] board){
        String fenStr = "";
        for (int y = 7; y >= 0; y--) {
            int spacer = 0;
            for (int x = 0; x < 8; x++) {
                Piece currentPiece = board[x][y];
                if(currentPiece != null){
                    fenStr += spacer == 0? "" : Integer.toString(spacer);
                    fenStr += getFenFromPiece(currentPiece);
                    spacer = 0;
                }else{
                    spacer++;
                }
            }
            if(y != 0){
                fenStr += spacer == 0 ? "/" : spacer + "/";
            }
        }
        return fenStr;
    }


    public static Piece[][] loadBoardFromFen(String fen){
        Piece[][] board = new Piece[8][8];
        String[] splitFenArr = fen.split("/");
        int x = 0;
        int y = 7;
        for(String rowStr : splitFenArr){
            char[] squareArray = rowStr.toCharArray();
            for(char square : squareArray){
                if(Character.isDigit(square)){
                    x += Character.getNumericValue(square);
                }else{
                    board[x][y] = getPieceFromFen(Character.toString(square), x, y);
                    x++;
                }
            }
            x = 0;
            y--;
        }
        return board;
    }


    public static String getFenFromPiece(Piece piece){
        String pieceStr;
        switch (piece.getPieceName()) {
            case BISHOP:
                pieceStr = "b";
                break;
            case KNIGHT:
                pieceStr = "n";
                break;
            case PAWN:
                pieceStr = "p";
                break;
            case ROOK:
                pieceStr = "r";
                break;
            case KING:
                pieceStr = "k";
                break;
            case QUEEN:
                pieceStr = "q";
                break;
            default:
                return null;
        }
        return piece.getColor() == Color.WHITE ? pieceStr.toUpperCase(Locale.ROOT) : pieceStr;
    }

    public static Piece getPieceFromFen(String pieceStr, int x, int y){
        PieceName pieceName;
        Color color;
        if(pieceStr.equalsIgnoreCase("p")){
            pieceName = PieceName.PAWN;
        }else if(pieceStr.equalsIgnoreCase("b")){
            pieceName = PieceName.BISHOP;
        }else if(pieceStr.equalsIgnoreCase(("q"))){
            pieceName = PieceName.QUEEN;
        }else if(pieceStr.equalsIgnoreCase("k")){
            pieceName = PieceName.KING;
        }else if(pieceStr.equalsIgnoreCase("r")){
            pieceName = PieceName.ROOK;
        }else if(pieceStr.equalsIgnoreCase("n")){
            pieceName = PieceName.KNIGHT;
        }else{
            pieceName = PieceName.KING;
            return null;
        }
        if(pieceStr.equals(pieceStr.toUpperCase(Locale.ROOT))){
            color = Color.WHITE;
        }else{
            color = Color.BLACK;
        }
        return new Piece(color, pieceName, x, y);
    }
}
