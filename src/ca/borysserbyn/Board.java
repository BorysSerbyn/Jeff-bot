package ca.borysserbyn;

import org.apache.commons.lang3.ArrayUtils;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

import static java.util.stream.Collectors.toCollection;

public class Board implements Cloneable, Serializable, Comparable {
    private static final long serialVersionUID = 1L;

    private ArrayList<Piece> pieces;
    private final int orientation; //1 if white on the first row
    private Color turn;
    private int turnCounter;
    private Tile[][] tiles;
    private Tile graveyard;
    private boolean[] castlingConditionsWhite;
    private boolean[] castlingConditionsBlack;
    private boolean[] enPassantConditionsWhite;
    private boolean[] enPassantConditionsBlack;
    private ArrayList<Move> moveHistory;
    private BoardState state;


    public Board(int orientation) {
        this.state = BoardState.NEUTRAL;
        this.turnCounter = 0;
        this.orientation = orientation;
        this.graveyard = new Tile(-1, -1, Color.WHITE);
        this.turn = Color.WHITE;
        initializeTiles();
        initializePieces();
        //initializeJeff();
        //initializePromotingTest();
        //initializeStalemateTest();
        //initializeinsufficientMatTest();
        //initializeCheckMateTest();

        this.moveHistory = new ArrayList<>();
        this.castlingConditionsWhite = new boolean[]{true, true, true};
        this.castlingConditionsBlack = new boolean[]{true, true, true};
        this.enPassantConditionsWhite = new boolean[]{false, false, false, false, false, false, false, false};
        this.enPassantConditionsBlack = new boolean[]{false, false, false, false, false, false, false, false};
    }

    public Board(ArrayList<Piece> pieces, int orientation, Color turn, int turnCounter, Tile[][] tiles, Tile graveyard, boolean[] castlingConditionsWhite, boolean[] castlingConditionsBlack, boolean[] enPassantConditionsWhite, boolean[] enPassantConditionsBlack, ArrayList<Move> moveHistory, BoardState state) {
        this.pieces = pieces;
        this.orientation = orientation;
        this.turn = turn;
        this.turnCounter = turnCounter;
        this.tiles = tiles;
        this.graveyard = graveyard;
        this.castlingConditionsWhite = castlingConditionsWhite;
        this.castlingConditionsBlack = castlingConditionsBlack;
        this.enPassantConditionsWhite = enPassantConditionsWhite;
        this.enPassantConditionsBlack = enPassantConditionsBlack;
        this.moveHistory = moveHistory;
        this.state = state;
    }

    @Override
    public int compareTo(Object o) {
        Board otherBoard = (Board) o;

        int centerPieceValue = (int) Math.signum(otherBoard.getCenterPieceValue() - this.getCenterPieceValue());

        int otherBoardCheckState = otherBoard.isKingChecked() ? 1 : -1;
        int otherBoardState = otherBoard.getState() != BoardState.NEUTRAL ? 1 : -1;
        int boardCheckState = this.isKingChecked() ? -1 : 1;
        int boardState = this.getState() != BoardState.NEUTRAL? -1 : 1;

        return 2*(boardState + boardCheckState + otherBoardState + otherBoardCheckState) + centerPieceValue;
    }

    @Override
    public Object clone() {
        Board board = null;
        try {
            board = (Board) super.clone();
        } catch (CloneNotSupportedException e) {
            board = new Board(pieces, orientation, turn, turnCounter, tiles, graveyard, castlingConditionsWhite, castlingConditionsBlack, enPassantConditionsWhite,
                    enPassantConditionsBlack, moveHistory, state);
        }


        Tile[][] clonedTiles = new Tile[8][8];
        ArrayList<Piece> clonedPieces = new ArrayList<Piece>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                clonedTiles[i][j] = (Tile) board.getTileByPosition(i, j).clone();
            }
        }

        Tile clonedGraveyard = (Tile) this.graveyard.clone();

        for (Piece piece : board.getPieces()) {
            Piece clonedPiece = (Piece) piece.clone();
            Tile clonedTile = clonedPiece.getTile();
            if (clonedTile.getX() != -1) { //check if the piece isnt in the graveyard
                clonedTiles[clonedTile.getX()][clonedTile.getY()] = clonedTile;
            }else{
                clonedPiece.setTile(clonedGraveyard);
            }
            clonedPieces.add(clonedPiece);
        }

        ArrayList<Move> clonedMoveHistory = new ArrayList<>();
        for (int i = 0; i < moveHistory.size(); i++) {
            Move clonedMove = (Move) moveHistory.get(i).clone();
            clonedMoveHistory.add(clonedMove);
        }

        board.moveHistory = clonedMoveHistory;
        board.castlingConditionsWhite = copyArrayOfBools(castlingConditionsWhite);
        board.castlingConditionsBlack = copyArrayOfBools(castlingConditionsBlack);
        board.enPassantConditionsWhite = copyArrayOfBools(enPassantConditionsWhite);
        board.enPassantConditionsBlack = copyArrayOfBools(enPassantConditionsBlack);
        board.pieces = clonedPieces;
        board.tiles = clonedTiles;
        board.graveyard = clonedGraveyard;

        return board;
    }

    public int getCenterPieceValue(){
        Color targetColor = getTurn() == Color.WHITE ? Color.BLACK : Color.WHITE;
        int[] inclusionSquare = new int[]{2,3,4,5};
        ArrayList<Piece> centerPieces = pieces.stream()
                .filter(piece -> ArrayUtils.contains(inclusionSquare, piece.getTile().getX()) && ArrayUtils.contains(inclusionSquare, piece.getTile().getY()))
                .collect(toCollection(ArrayList::new));
        int centerBoardValue = getSubsetValueByColor(targetColor, centerPieces);
        return centerBoardValue;
    }


    //copys an array of bools (conditions) for the clone function
    public boolean[] copyArrayOfBools(boolean[] targetArray) {
        boolean[] copiedArray = new boolean[targetArray.length];
        for (int i = 0; i < targetArray.length; i++) {
            copiedArray[i] = targetArray[i];
        }
        return copiedArray;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public BoardState getState() {
        return state;
    }

    public void setState(BoardState state) {
        this.state = state;
    }

    public int getOrientation() {
        return orientation;
    }

    public Tile getGraveyard() {
        return graveyard;
    }

    public Color getTurn() {
        return turn;
    }

    //also checks calls endgame detection to change state.
    public void toggleTurn() {
        increaseTurnCount();
        if (turn == Color.BLACK) {
            for (boolean cond : enPassantConditionsWhite) {
                cond = false;
            }
            turn = Color.WHITE;
        } else {
            for (boolean cond : enPassantConditionsBlack) {
                cond = false;
            }
            turn = Color.BLACK;
        }
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public void increaseTurnCount() {
        turnCounter++;
    }

    public ArrayList<Piece> getPiecesByColor(Color color) {
        return pieces.stream()
                .filter(piece -> piece.getColor() == color)
                .collect(toCollection(ArrayList::new));
    }

    public Piece getPieceByTile(Tile tile) {
        return pieces.stream()
                .filter(piece -> piece.getTile() == tile)
                .findFirst()
                .orElse(null);
    }

    public Piece getPieceByClone(Piece clonedPiece){
        return pieces.stream()
                .filter(piece -> piece.equals(clonedPiece))
                .findFirst()
                .orElse(null);
    }

    public Piece getPieceByName(PieceName pieceName, Color color) {
        return pieces.stream()
                .filter(piece -> piece.getPieceName() == pieceName)
                .filter(piece -> piece.getColor() == color)
                .findFirst()
                .orElse(null);
    }

    public Tile getTileByPosition(int x, int y) {
        return tiles[x][y];
    }

    public Tile getTileByClone(Tile clonedTile){
        return tiles[clonedTile.getX()][clonedTile.getY()];
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }

    public ArrayList<Piece> getUneatenPieces() {
        return pieces.stream()
                .filter(piece -> piece.getTile() != graveyard)
                .collect(toCollection(ArrayList::new));
    }

    public ArrayList<Piece> getEatenPieces() {
        return pieces.stream()
                .filter(piece -> piece.getTile() == graveyard)
                .collect(toCollection(ArrayList::new));
    }

    public ArrayList<Piece> getUneatenPiecesByColor(Color color) {
        return pieces.stream()
                .filter(piece -> piece.getTile() != graveyard && piece.getColor() == color)
                .collect(toCollection(ArrayList::new));
    }

    //adds last move to appropriate array to track threefold repetitions
    public void addLastMove(Piece piece, Tile tile) {
        Move move = new Move(piece, tile);
        Move archivedMove = (Move) move.clone();
        moveHistory.add(archivedMove);
    }

    public Move getLastMove(){
        return moveHistory.get(moveHistory.size()-1);
    }


    /*
    The following methods are useful for the AI
     */
    public ArrayList<Board> getLegalBoardsByColor(Color color){
        ArrayList<Move> allLegalMoves = this.getLegalMovesByColor(this.getTurn());
        ArrayList<Board> allLegalBoards = new ArrayList<>();
        for (Move legalMove : allLegalMoves) {
            Board clonedBoard = (Board) this.clone();
            Piece clonedPiece = clonedBoard.getPieceByClone(legalMove.getPiece());
            Tile clonedTile = clonedBoard.getTileByClone(legalMove.getTile());
            clonedBoard.movePiece(clonedPiece, clonedTile);
            allLegalBoards.add(clonedBoard);
        }
        return allLegalBoards;
    }

    public ArrayList<Move> getLegalMovesByColor(Color color){
        ArrayList<Move> legalMovesList = new ArrayList<>();
        for (Piece piece: getUneatenPiecesByColor(color)) {
            legalMovesList.addAll(getLegalMovesByPiece(piece));
        }
        return legalMovesList;
    }

    public ArrayList<Move> getLegalMovesByPiece(Piece piece) {
        ArrayList<Move> legalMovesList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isMoveLegal(piece, getTileByPosition(i, j))) {
                    Move move = new Move(piece, getTileByPosition(i, j));
                    legalMovesList.add(move);
                }
            }
        }
        return legalMovesList;
    }

    public int getBoardValueByColor(Color color){
        int value = 0;
        for (Piece piece:getUneatenPieces()) {
            if(piece.getColor() == color){
                value += piece.getValue();
            }else{
                value -= piece.getValue();
            }
        }
        return value;
    }

    public int getSubsetValueByColor(Color color, ArrayList<Piece> pieces){
        int value = 0;
        for (Piece piece:pieces) {
            if(piece.getColor() == color){
                value += piece.getValue();
            }else{
                value -= piece.getValue();
            }
        }
        return value;
    }



    /*
    End game detection.
     */
    public boolean isGameOver() {
        if (isBoardInCheckMate()) {
            return true;
        } else if (isBoardInStaleMate()) {
            return true;
        }
        return false;
    }

    public boolean isBoardInCheckMate() {
        Piece king = getPieceByName(PieceName.KING, turn);
        if (!isPieceThreatened(king)) {
            return false;
        }
        for (Piece piece : getUneatenPieces()) {
            if (canPieceMove(piece)) {
                return false;
            }
        }
        setState(BoardState.CHECKMATE);
        return true;
    }

    public boolean isBoardInStaleMate() {
        if (noLegalMovesCheck()) {
            setState(BoardState.STALEMATE);
            System.out.println("no legal moves");
            return true;
        }
        if (insufficientMaterialCheck()) {
            setState(BoardState.STALEMATE);
            System.out.println("insufficient material");
            return true;
        }
        if (threefoldRepetitionCheck()) {
            setState(BoardState.STALEMATE);
            System.out.println("threefold repetition");
            return true;
        }
        return false;
    }

    //first way to stalemate
    public boolean noLegalMovesCheck() {
        Piece king = getPieceByName(PieceName.KING, turn);
        if (canPieceMove(king)) {
            return false;
        }
        for (Piece piece : getUneatenPieces()) {
            if (piece.getColor() == turn && canPieceMove(piece)) {
                return false;
            }
        }
        return true;
    }

    //second way to stalemate
    public boolean insufficientMaterialCheck() {
        if (pieces.size() <= 4) {
            ArrayList<Piece> blackPieces = getUneatenPiecesByColor(Color.BLACK);
            ArrayList<Piece> whitePieces = getUneatenPiecesByColor(Color.WHITE);
            if (blackPieces.size() == 3) { //if black has 3 pieces, checks if both of them are knights (stalemate)
                for (Piece piece : blackPieces) {
                    PieceName name = piece.getPieceName();
                    if (name != PieceName.KING && name != PieceName.KNIGHT) {
                        return false;
                    }
                }
            } else if (whitePieces.size() == 3) {//if white has 3 pieces, checks if both of them are knights (stalemate)
                for (Piece piece : whitePieces) {
                    PieceName name = piece.getPieceName();
                    if (name != PieceName.KING && name != PieceName.KNIGHT) {
                        return false;
                    }
                }
            }
            for (Piece piece : getUneatenPieces()) {
                PieceName name = piece.getPieceName();
                if (name != PieceName.KING && name != PieceName.BISHOP && name != PieceName.KNIGHT) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean threefoldRepetitionCheck() {
        if (moveHistory.size() < 8) {
            return false;
        }

        int firstMoveIndex = moveHistory.size() < 8 ? 0 : moveHistory.size() - 8;
        Move whiteFirst = moveHistory.get(firstMoveIndex);
        Move blackFirst = moveHistory.get(firstMoveIndex + 1);


        for (int i = firstMoveIndex; i < moveHistory.size(); i = i + 4) {
            if (!moveHistory.get(i).equals(whiteFirst)) {
                return false;
            }
            if (!moveHistory.get(i + 1).equals(blackFirst)) {
                return false;
            }
        }
        return true;
    }

    public boolean canPieceMove(Piece piece) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isMoveLegal(piece, getTileByPosition(i, j))) {
                    return true;
                }
            }
        }
        return false;
    }


    /*
    Manage moving pieces (any kind of movement)
     */
    public void movePiece(Piece piece, Tile tile) {
        Piece targetPiece = getPieceByTile(tile);
        setState(BoardState.NEUTRAL);
        updateEnPassantConditions(piece.getColor());


        if (targetPiece != null) {//if a piece is to be eaten
            targetPiece.discardPiece(graveyard);
            setState(BoardState.PIECE_EATEN);

            if (piece.getPieceName() == PieceName.PAWN && isPawnPromotionLegal(piece, tile)) {
                setState(BoardState.PROMOTING_AND_EATING);
            }
        }
        if (piece.getPieceName() == PieceName.PAWN && isPawnMove2Legal(piece, tile)) {//is this piece a pawn moving up 2?
            boolean[] targetConditions = piece.getColor() == Color.WHITE ? enPassantConditionsWhite : enPassantConditionsBlack;
            targetConditions[piece.getTile().getX()] = true;
        }
        if (piece.getPieceName() == PieceName.PAWN && isPawnEnPassantLegal(piece, tile)) {//is en passant legal?
            getPieceByTile(getTileByPosition(tile.getX(), piece.getTile().getY())).discardPiece(graveyard);
            setState(BoardState.EN_PASSANT);
        }
        if (piece.getPieceName() == PieceName.PAWN && isPawnPromotionLegal(piece, tile)) {
            setState(BoardState.PROMOTING_PAWN);
        }
        if (piece.getPieceName() == PieceName.KING && isCastlingLegal(piece, tile)) {//is the piece a king and castling?
            castlingMove(piece, tile);
        }
        updateCastlingConditions(piece);
        addLastMove(piece, tile);
        piece.setTile(tile);
        toggleTurn();
    }

    public void castlingMove(Piece piece, Tile tile) {
        Tile rookTile;
        int rookXMove;
        int x = piece.getTile().getX();
        int signedXMove = tile.getX() - x;

        if (orientation == 1) {
            rookXMove = signedXMove < 0 ? 2 : -3; //short if true
            rookTile = signedXMove < 0 ? getTileByPosition(0, piece.getTile().getY()) : getTileByPosition(7, piece.getTile().getY());
        } else {
            rookXMove = signedXMove > 0 ? -2 : 3; //short if true
            rookTile = signedXMove > 0 ? getTileByPosition(0, piece.getTile().getY()) : getTileByPosition(7, piece.getTile().getY());
        }
        Piece rook = this.getPieceByTile(rookTile);
        rook.setTile(getTileByPosition(rookTile.getX() + rookXMove, rookTile.getY()));
        setState(Math.abs(rookXMove) == 2 ? BoardState.CASTLING_SHORT : BoardState.CASTLING_LONG);
    }

    //updates castling condition based on the movement of a given piece (did rooks or king move)
    public void updateCastlingConditions(Piece piece) {
        boolean[] castlingConditions = piece.getColor() == Color.WHITE ? castlingConditionsWhite : castlingConditionsBlack;

        if (piece.getPieceName() == PieceName.KING) {//turns off castling conditions if king moves.
            for (int i = 0; i < 3; i++) {
                castlingConditions[i] = false;
            }
        }

        if (piece.getPieceName() == PieceName.ROOK) {//turns off castling condition for a specific rook if it moves.
            if (orientation == 1) {
                if (piece.getTile().getX() == 0) {
                    castlingConditions[0] = false;
                } else {
                    castlingConditions[2] = false;
                }
            } else {
                if (piece.getTile().getX() == 7) {
                    castlingConditions[0] = false;
                } else {
                    castlingConditions[2] = false;
                }
            }
        }
    }

    //updates castling condition based on the movement of a given piece (did rooks or king move)
    public void updateEnPassantConditions(Color color) {
        boolean[] enPassantConditions = color == Color.WHITE ? enPassantConditionsWhite : enPassantConditionsBlack;

        for (int i = 0; i < 8; i++) {
            enPassantConditions[i] = false;
        }
    }


    public void promotePawn(Piece piece, PieceName pieceName) {
        piece.setPieceName(pieceName);
    }


    /*
    Tests to decide if a move is legal
     */
    public boolean isMoveLegal(Piece piece, Tile tile) {
        /*
        The next few condition apply to any piece
         */

        if (piece.getColor() != turn) {//is it the colors turn to move?
            return false;
        }
        for (Piece boardPiece : pieces) {//is the piece trying to eat its own color? (includes himself)
            if (boardPiece.getTile() == tile && boardPiece.getColor() == piece.getColor()) {
                return false;
            }
        }
        if (willKingBeChecked(piece, tile)) {//checks if a move will cause its own king to be checked.
            return false;
        }
        /*
        Switch checks if the piece is behaving according to its type.
         */
        switch (piece.getPieceName()) {
            case PAWN:
                return isPawnMoveLegal(piece, tile);
            case KNIGHT:
                return isKnightMoveLegal(piece, tile);
            case BISHOP:
                return isBishopMoveLegal(piece, tile);
            case ROOK:
                return isRookMoveLegal(piece, tile);
            case KING:
                return isKingMoveLegal(piece, tile) || isCastlingLegal(piece, tile);
            case QUEEN:
                return isQueenMoveLegal(piece, tile);
            default:
                return true;
        }
    }

    public boolean isKnightMoveLegal(Piece piece, Tile tile) {
        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int signedXMove = tile.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        if (xMove != 2 && xMove != 1) {
            return false;
        }

        if (xMove + yMove != 3) {
            return false;
        }

        return true;
    }

    public boolean isBishopMoveLegal(Piece piece, Tile tile) {
        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int signedXMove = tile.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        if (xMove != yMove) {
            return false;
        }

        if (isPieceInTheWay(piece, tile)) {
            return false;
        }
        return true;
    }

    public boolean isRookMoveLegal(Piece piece, Tile tile) {
        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int signedXMove = tile.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        if (!(xMove == 0 || yMove == 0)) {
            return false;
        }

        if (isPieceInTheWay(piece, tile)) {
            return false;
        }
        return true;
    }

    public boolean isKingMoveLegal(Piece piece, Tile tile) {
        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int signedXMove = tile.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        if (isRookMoveLegal(piece, tile)) {
            if (yMove + xMove != 1) {
                return false;
            }
        } else if (isBishopMoveLegal(piece, tile)) {
            if (yMove + xMove != 2) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean isQueenMoveLegal(Piece piece, Tile tile) {
        if (isRookMoveLegal(piece, tile)) {
            return true;
        } else if (isBishopMoveLegal(piece, tile)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isPawnMoveLegal(Piece piece, Tile tile) {
        if (!isPawnOrientationLegal(piece, tile)) {
            return false;
        }
        if (isPawnMove1Legal(piece, tile) || isPawnMove2Legal(piece, tile) || isPawnEatLegal(piece, tile) || isPawnEnPassantLegal(piece, tile)) {
            return true;
        } else {
            return false;
        }
    }

    //checks if pawn is moving in the right direction
    public boolean isPawnOrientationLegal(Piece piece, Tile tile) {
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int yMove = Math.abs(signedYMove);

        if (orientation == 1) {
            if (piece.getColor() == Color.BLACK) {
                if (signedYMove > 0) {
                    return false;
                }
            } else {
                if (signedYMove < 0) {
                    return false;
                }
            }
        } else {
            if (piece.getColor() == Color.WHITE) {
                if (signedYMove > 0) {
                    return false;
                }
            } else {
                if (signedYMove < 0) {
                    return false;
                }
            }
        }

        return true;
    }

    //checks if the pawn is moving 1 square
    public boolean isPawnMove1Legal(Piece piece, Tile tile) {
        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int signedXMove = tile.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        for (Piece boardPiece : pieces) { //is piece in front of pawn
            if (boardPiece.getTile() == tile) {
                return false;
            }
        }
        if (xMove != 0) { // does pawn move diagonaly
            return false;
        }
        if (yMove != 1) {
            return false;
        }
        return true;
    }

    //checks if the pawn is moving 2 squares as its first move.
    public boolean isPawnMove2Legal(Piece piece, Tile tile) {
        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int signedXMove = tile.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        for(Piece anyPiece: getUneatenPieces()){
            if(anyPiece.getTile().equals(tile)){
                return false;
            }
        }

        if (xMove != 0) {
            return false;
        }
        if(!isRookMoveLegal(piece,tile)){
            return false;
        }
        if (orientation == 1) {
            if (piece.getColor() == Color.WHITE) {
                if (piece.getTile().getY() != 1) {
                    return false;
                }
            } else {
                if (piece.getTile().getY() != 6) {
                    return false;
                }
            }
        } else {
            if (piece.getColor() == Color.BLACK) {
                if (piece.getTile().getY() != 1) {
                    return false;
                }
            } else {
                if (piece.getTile().getY() != 6) {
                    return false;
                }
            }
        }

        if (yMove != 2) {
            return false;
        }

        return true;
    }

    //checks if normal eat is legal
    public boolean isPawnEatLegal(Piece piece, Tile tile) {
        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int signedXMove = tile.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        boolean isPieceThere = false;
        for (Piece boardPiece : pieces) {
            if (boardPiece.getTile() == tile) {
                isPieceThere = true;
            }
        }

        if (!isPieceThere) {
            return false;
        } else {
            if (isBishopMoveLegal(piece, tile)) {
                if (xMove != 1 || yMove != 1) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    //checks if en passant is legal (not implemented yet)
    public boolean isPawnEnPassantLegal(Piece piece, Tile tile) {
        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int signedXMove = tile.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        int expectedY = !(orientation == 1 ^ piece.getColor() == Color.WHITE) ? 4 : 3; //orientation 1 xnor white
        boolean[] targetConditions = piece.getColor() == Color.WHITE ? enPassantConditionsBlack : enPassantConditionsWhite;
        boolean targetCondition = targetConditions[tile.getX()];
        Piece targetPiece = getPieceByTile(getTileByPosition(tile.getX(), y));

        if (getPieceByTile(tile) != null) { //is there a piece at the destination tile
            return false;
        }
        if (xMove != 1 || yMove != 1) { //is the move 1 square in each direction
            return false;
        }
        if(!targetCondition){ //has the target pawn moved 2 squares last turn
            return false;
        }
        if(expectedY != y){ //is the piece at its expected y position
            return false;
        }
        if(targetPiece == null){
            return false;
        }else if(targetPiece.getPieceName() != PieceName.PAWN){
            return false;
        }

        return true;
    }

    public boolean isPawnPromotionLegal(Piece piece, Tile tile) {
        if (!isPawnOrientationLegal(piece, tile)) {
            return false;
        }
        if (tile.getY() != 7 && tile.getY() != 0) {
            return false;
        }
        return true;
    }

    //checks if king is allowed to castle
    public boolean isCastlingLegal(Piece piece, Tile tile) {

        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int signedXMove = tile.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        boolean[] casltingConditions = piece.getColor() == Color.WHITE ? castlingConditionsWhite : castlingConditionsBlack;
        boolean kingCondition = casltingConditions[1];
        boolean castleCondition;
        Tile castleTile;
        Tile inBetweenTile = getTileByPosition(signedXMove / 2 + x, y);

        if (orientation == 1) {
            castleCondition = signedXMove < 0 ? casltingConditions[0] : casltingConditions[2]; //short if true
            castleTile = signedXMove < 0 ? getTileByPosition(0, piece.getTile().getY()) : getTileByPosition(7, piece.getTile().getY());
        } else {
            castleCondition = signedXMove > 0 ? casltingConditions[0] : casltingConditions[2];
            castleTile = signedXMove > 0 ? getTileByPosition(0, piece.getTile().getY()) : getTileByPosition(7, piece.getTile().getY());
        }

        if(yMove != 0){
            return false;
        }
        if (isPieceThreatened(piece)) { //is king checked
            return false;
        }
        if (!kingCondition || !castleCondition) {//has the king or the tower moved
            return false;
        }
        if (xMove != 2) {//is the king trying to move more than 2 squares
            return false;
        }
        if (getPieceByTile(castleTile) == null) {
            return false;
        }
        if (isPieceInTheWay(piece, castleTile)) {//is a piece between the king and the rook
            return false;
        }
        if (willKingBeChecked(piece, inBetweenTile)) {//will the king get checked in between movements.
            return false;
        }
        return true;
    }

    //checks if a piece is in the trajectory (line or cross)
    public boolean isPieceInTheWay(Piece piece, Tile tile) {
        int yMove = tile.getY() - piece.getTile().getY();
        int xMove = tile.getX() - piece.getTile().getX();
        int move = Math.abs(xMove) > Math.abs(yMove) ? xMove : yMove;

        for (int i = 1; i < Math.abs(move); i++) {
            int y = piece.getTile().getY() + i * (int) Math.signum(yMove);
            int x = piece.getTile().getX() + i * (int) Math.signum(xMove);
            Tile targetTile = getTileByPosition(x, y);
            if (getPieceByTile(targetTile) != null) {
                return true;
            }
        }

        return false;
    }

    //checks if a piece is threatened
    public boolean isPieceThreatened(Piece piece) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Tile targetTile = getTileByPosition(i, j);
                if (isQueenMoveLegal(piece, targetTile)) {
                    Piece targetPiece = getPieceByTile(targetTile);
                    if (targetPiece != null && targetPiece.getPieceName() == PieceName.QUEEN && targetPiece.getColor() != piece.getColor()) {
                        //System.out.println(piece.toString() + " is threatened by :" + targetPiece.toString());
                        return true;
                    }
                }
                if (isBishopMoveLegal(piece, targetTile)) {
                    Piece targetPiece = getPieceByTile(targetTile);
                    if (targetPiece != null && targetPiece.getPieceName() == PieceName.BISHOP && targetPiece.getColor() != piece.getColor()) {
                        //System.out.println(piece.toString() + " is threatened by :" + targetPiece.toString());
                        return true;
                    }
                }
                if (isRookMoveLegal(piece, targetTile)) {
                    if (targetTile.getY() == 7 && targetTile.getX() == 0) {
                    }
                    Piece targetPiece = getPieceByTile(targetTile);
                    if (targetPiece != null && targetPiece.getPieceName() == PieceName.ROOK && targetPiece.getColor() != piece.getColor()) {
                        //System.out.println(piece.toString() + " is threatened by :" + targetPiece.toString());
                        return true;
                    }
                }
                if (isKnightMoveLegal(piece, targetTile)) {
                    Piece targetPiece = getPieceByTile(targetTile);
                    if (targetPiece != null && targetPiece.getPieceName() == PieceName.KNIGHT && targetPiece.getColor() != piece.getColor()) {
                        //System.out.println(piece.toString() + " is threatened by :" + targetPiece.toString());
                        return true;
                    }
                }
                if (isKingMoveLegal(piece, targetTile)) {
                    Piece targetPiece = getPieceByTile(targetTile);
                    if (targetPiece != null && targetPiece.getPieceName() == PieceName.KING && targetPiece.getColor() != piece.getColor()) {
                        //System.out.println(piece.toString() + " is threatened by :" + targetPiece.toString());
                        return true;
                    }
                }
                if (isPawnEatLegal(piece, targetTile) && isPawnOrientationLegal(piece, targetTile)) {
                    Piece targetPiece = getPieceByTile(targetTile);
                    if (targetPiece != null && targetPiece.getPieceName() == PieceName.PAWN && targetPiece.getColor() != piece.getColor()) {
                        //System.out.println(piece.toString() + " is threatened by :" + targetPiece.toString());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isKingChecked(){
        Piece king = this.getPieceByName(PieceName.KING, turn);
        if (this.isPieceThreatened(king)) {
            return true;
        }
        return false;
    }

    //checks if a move will cause its own king to be checked.
    public boolean willKingBeChecked(Piece piece, Tile tile) {
        Board boardDeepCopy = (Board) this.clone();
        Tile tileDeepCopy = boardDeepCopy.getTileByPosition(piece.getTile().getX(), piece.getTile().getY());
        Tile destinationTileDC = boardDeepCopy.getTileByPosition(tile.getX(), tile.getY());
        Piece pieceDeepCopy = boardDeepCopy.getPieceByTile(tileDeepCopy);
        boardDeepCopy.movePiece(pieceDeepCopy, destinationTileDC);
        Piece kingDeepCopy = boardDeepCopy.getPieceByName(PieceName.KING, piece.getColor());

        if (boardDeepCopy.isPieceThreatened(kingDeepCopy)) {
            return true;
        }
        return false;
    }


    /*
    Initializers
     */
    public void initializeTiles() {
        tiles = new Tile[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((j + i) % 2 == 0) {
                    tiles[i][j] = new Tile(i, j, Color.WHITE);
                } else {
                    tiles[i][j] = new Tile(i, j, Color.BLACK);
                }
            }
        }
    }

    public void initializePieces() {
        pieces = new ArrayList<Piece>();
        for (int i = 0; i < 8; i++) {
            pieces.add(new Piece(Color.WHITE, PieceName.PAWN, getTileByPosition(i, 1)));
        }
        for (int i = 0; i < 8; i++) {
            pieces.add(new Piece(Color.BLACK, PieceName.PAWN, getTileByPosition(i, 6)));
        }
        pieces.add(new Piece(Color.WHITE, PieceName.ROOK, getTileByPosition(0, 0)));
        pieces.add(new Piece(Color.WHITE, PieceName.KNIGHT, getTileByPosition(1, 0)));
        pieces.add(new Piece(Color.WHITE, PieceName.BISHOP, getTileByPosition(2, 0)));
        pieces.add(new Piece(Color.WHITE, PieceName.KING, getTileByPosition(3, 0)));
        pieces.add(new Piece(Color.WHITE, PieceName.QUEEN, getTileByPosition(4, 0)));
        pieces.add(new Piece(Color.WHITE, PieceName.BISHOP, getTileByPosition(5, 0)));
        pieces.add(new Piece(Color.WHITE, PieceName.KNIGHT, getTileByPosition(6, 0)));
        pieces.add(new Piece(Color.WHITE, PieceName.ROOK, getTileByPosition(7, 0)));

        pieces.add(new Piece(Color.BLACK, PieceName.ROOK, getTileByPosition(0, 7)));
        pieces.add(new Piece(Color.BLACK, PieceName.KNIGHT, getTileByPosition(1, 7)));
        pieces.add(new Piece(Color.BLACK, PieceName.BISHOP, getTileByPosition(2, 7)));
        pieces.add(new Piece(Color.BLACK, PieceName.KING, getTileByPosition(3, 7)));
        pieces.add(new Piece(Color.BLACK, PieceName.QUEEN, getTileByPosition(4, 7)));
        pieces.add(new Piece(Color.BLACK, PieceName.BISHOP, getTileByPosition(5, 7)));
        pieces.add(new Piece(Color.BLACK, PieceName.KNIGHT, getTileByPosition(6, 7)));
        pieces.add(new Piece(Color.BLACK, PieceName.ROOK, getTileByPosition(7, 7)));
    }

    public void initializeJeff() {
        pieces = new ArrayList<Piece>();
        pieces.add(new Piece(Color.WHITE, PieceName.KING, getTileByPosition(3, 0)));
        pieces.add(new Piece(Color.BLACK, PieceName.KING, getTileByPosition(3, 7)));
        pieces.add(new Piece(Color.WHITE, PieceName.PAWN, getTileByPosition(0, 1)));
        pieces.add(new Piece(Color.BLACK, PieceName.PAWN, getTileByPosition(7, 6)));
        pieces.add(new Piece(Color.BLACK, PieceName.ROOK, getTileByPosition(5, 6)));
        pieces.add(new Piece(Color.WHITE, PieceName.ROOK, getTileByPosition(4, 1)));
    }

    public void initializePromotingTest() {
        pieces = new ArrayList<Piece>();
        pieces.add(new Piece(Color.WHITE, PieceName.KING, getTileByPosition(3, 0)));
        pieces.add(new Piece(Color.BLACK, PieceName.KING, getTileByPosition(3, 7)));
        pieces.add(new Piece(Color.WHITE, PieceName.PAWN, getTileByPosition(0, 1)));
        pieces.add(new Piece(Color.BLACK, PieceName.PAWN, getTileByPosition(7, 6)));
    }

    public void initializeStalemateTest() {
        pieces = new ArrayList<Piece>();
        pieces.add(new Piece(Color.WHITE, PieceName.KING, getTileByPosition(0, 7)));
        pieces.add(new Piece(Color.BLACK, PieceName.KING, getTileByPosition(7, 0)));
        pieces.add(new Piece(Color.WHITE, PieceName.QUEEN, getTileByPosition(4, 1)));
    }

    public void initializeinsufficientMatTest() {
        pieces = new ArrayList<Piece>();
        pieces.add(new Piece(Color.WHITE, PieceName.KING, getTileByPosition(0, 7)));
        pieces.add(new Piece(Color.BLACK, PieceName.KING, getTileByPosition(7, 0)));
        pieces.add(new Piece(Color.WHITE, PieceName.PAWN, getTileByPosition(1, 6)));
        pieces.add(new Piece(Color.WHITE, PieceName.QUEEN, graveyard));
    }

    public void initializeCheckMateTest() {
        pieces = new ArrayList<Piece>();
        pieces.add(new Piece(Color.WHITE, PieceName.KING, getTileByPosition(0, 7)));
        pieces.add(new Piece(Color.BLACK, PieceName.KING, getTileByPosition(7, 0)));
        pieces.add(new Piece(Color.WHITE, PieceName.QUEEN, getTileByPosition(4, 1)));
        pieces.add(new Piece(Color.WHITE, PieceName.ROOK, getTileByPosition(6, 2)));
    }
}

