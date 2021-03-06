package ca.borysserbyn.mechanics;

import java.util.ArrayList;
import java.util.Locale;

public class FenUtils {
    
    public static String createFenFromGame(Game game) {
        if(game.getOrientation() != 1){
            return null;
        }
        String fenStr = createFenFromBoard(game.getBoard());

        fenStr += game.getTurn() == Color.WHITE ? " w" : " b";

        String castlingStr = createCaslingFen(game);
        fenStr += castlingStr.equals(" ") ? " -" : castlingStr;

        String enPassantStr = createEnPassantFen(game);
        fenStr += enPassantStr.equals(" ") ? " -" : enPassantStr;

        fenStr += " " + game.getFiftyMoveClock();

        fenStr += " " + (int) Math.ceil(((double)game.getTurnCounter()+1)/2);
        return fenStr;
    }
    
    public static Game createGameFromFen(String fenStr){
        String[] splitFen = fenStr.split(" ");
        Game game = new Game(1);

        game.setBoard(createBoardFromFen(splitFen[0]));

        ArrayList<Piece> pieces = new ArrayList();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = game.getBoard()[i][j];
                if(piece != null){
                    pieces.add(piece);
                }
            }
        }
        game.setPieces(pieces);

        game.setTurn(splitFen[1].equals("w") ? Color.WHITE : Color.BLACK);

        game.getCastlingConditionsWhite()[0] = splitFen[2].contains("K") ? true : false;
        game.getCastlingConditionsWhite()[1] = true;
        game.getCastlingConditionsWhite()[2] = splitFen[2].contains("Q") ? true : false;
        game.getCastlingConditionsBlack()[0] = splitFen[2].contains("k") ? true : false;
        game.getCastlingConditionsBlack()[1] = true;
        game.getCastlingConditionsBlack()[2] = splitFen[2].contains("q") ? true : false;

        int letterValue = splitFen[3].charAt(0);
        int index = letterValue-97;

        if(splitFen[3].contains("3")){
            game.getEnPassantConditionsWhite()[index] = true;
        }else if(splitFen[3].contains("6")){
            game.getEnPassantConditionsBlack()[index] = true;
        }

        game.setFiftyMoveClock(Integer.valueOf(splitFen[4]));
        int fullMoves = Integer.valueOf(splitFen[4]);
        int turnCount = game.getTurn() == Color.WHITE ? (fullMoves-1)/2 : fullMoves/2;
        game.setTurnCounter(turnCount);
        return game;
    }

    public static String createFenFromBoard(Piece[][] board) {
        String fenStr = "";
        for (int y = 7; y >= 0; y--) {
            int spacer = 0;
            for (int x = 0; x < 8; x++) {
                Piece currentPiece = board[x][y];
                if (currentPiece != null) {
                    fenStr += spacer == 0 ? "" : Integer.toString(spacer);
                    fenStr += createFenFromPiece(currentPiece);
                    spacer = 0;
                } else {
                    spacer++;
                }
            }
            if (y != 0) {
                fenStr += spacer == 0 ? "/" : spacer + "/";
            }
        }
        return fenStr;
    }


    public static Piece[][] createBoardFromFen(String fen) {
        Piece[][] board = new Piece[8][8];
        String[] splitFenArr = fen.split("/");
        int x = 0;
        int y = 7;
        for (String rowStr : splitFenArr) {
            char[] squareArray = rowStr.toCharArray();
            for (char square : squareArray) {
                if (Character.isDigit(square)) {
                    x += Character.getNumericValue(square);
                } else {
                    board[x][y] = getPieceFromFen(Character.toString(square), x, y);
                    x++;
                }
            }
            x = 0;
            y--;
        }
        return board;
    }

    public static String createCaslingFen(Game game){
        String fenStr = " ";
        fenStr += game.getCastlingConditionsWhite()[0] ? "K" : "";
        fenStr += game.getCastlingConditionsWhite()[1] ? "Q" : "";
        fenStr += game.getCastlingConditionsBlack()[0] ? "k" : "";
        fenStr += game.getCastlingConditionsBlack()[1] ? "q" : "";
        return fenStr;
    }

    public static String createEnPassantFen(Game game){
        String fenStr = " ";
        for (int i = 0; i < 8; i++) {
            fenStr += game.getEnPassantConditionsWhite()[i] ? (char)(65+i) + "3" : "";
            fenStr += game.getEnPassantConditionsBlack()[i] ? (char)(65+i) + "6" : "";
        }
        return fenStr;
    }

    public static String createFenFromPiece(Piece piece) {
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

    public static Piece getPieceFromFen(String pieceStr, int x, int y) {
        PieceName pieceName;
        Color color;
        if (pieceStr.equalsIgnoreCase("p")) {
            pieceName = PieceName.PAWN;
        } else if (pieceStr.equalsIgnoreCase("b")) {
            pieceName = PieceName.BISHOP;
        } else if (pieceStr.equalsIgnoreCase(("q"))) {
            pieceName = PieceName.QUEEN;
        } else if (pieceStr.equalsIgnoreCase("k")) {
            pieceName = PieceName.KING;
        } else if (pieceStr.equalsIgnoreCase("r")) {
            pieceName = PieceName.ROOK;
        } else if (pieceStr.equalsIgnoreCase("n")) {
            pieceName = PieceName.KNIGHT;
        } else {
            pieceName = PieceName.KING;
            return null;
        }
        if (pieceStr.equals(pieceStr.toUpperCase(Locale.ROOT))) {
            color = Color.WHITE;
        } else {
            color = Color.BLACK;
        }
        return new Piece(color, pieceName, x, y);
    }
}
