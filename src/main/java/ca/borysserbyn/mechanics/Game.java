package ca.borysserbyn.mechanics;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import static java.util.stream.Collectors.toCollection;

public class Game implements Cloneable, Serializable, Comparable {
    private static final long serialVersionUID = 1L;

    private ArrayList<Piece> pieces;
    private final int orientation; //1 if white on the first row
    private Color turn;
    private int turnCounter;
    private int fiftyMoveClock;
    private Piece[][] board = new Piece[8][8];

    private int[] graveyard;
    private int whiteCastleState; // 0: not castled, 1: short, 2: long
    private int blackCastleState;
    private boolean[] castlingConditionsWhite;
    private boolean[] castlingConditionsBlack;
    private boolean[] enPassantConditionsWhite;
    private boolean[] enPassantConditionsBlack;


    private ArrayList<Move> moveHistory;
    private GameState state;
    private int seed;


    public Game(int orientation) {
        this.state = GameState.NEUTRAL;
        this.turnCounter = 0;
        this.orientation = orientation;
        this.graveyard = new int[]{-1, -1};
        this.turn = Color.WHITE;
        this.whiteCastleState = 0;
        this.blackCastleState = 0;
        seed = new Random().nextInt();
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
        buildBoard();
    }

    public Game(ArrayList<Piece> pieces, int orientation, Color turn, int turnCounter, int[] graveyard, int whiteCastleState, int blackCastleState, boolean[] castlingConditionsWhite, boolean[] castlingConditionsBlack, boolean[] enPassantConditionsWhite, boolean[] enPassantConditionsBlack, ArrayList<Move> moveHistory, GameState state, int seed) {
        this.pieces = pieces;
        this.orientation = orientation;
        this.turn = turn;
        this.turnCounter = turnCounter;
        this.graveyard = graveyard;
        this.whiteCastleState = whiteCastleState;
        this.blackCastleState = blackCastleState;
        this.castlingConditionsWhite = castlingConditionsWhite;
        this.castlingConditionsBlack = castlingConditionsBlack;
        this.enPassantConditionsWhite = enPassantConditionsWhite;
        this.enPassantConditionsBlack = enPassantConditionsBlack;
        this.moveHistory = moveHistory;
        this.state = state;
        this.seed = seed;
    }

    @Override
    public Object clone() {
        Game clonedGame = null;
        try {
            clonedGame = (Game) super.clone();
        } catch (CloneNotSupportedException e) {
            clonedGame = new Game(pieces, orientation, turn, turnCounter, graveyard, whiteCastleState, blackCastleState, castlingConditionsWhite, castlingConditionsBlack, enPassantConditionsWhite,
                    enPassantConditionsBlack, moveHistory, state, seed);
        }

        ArrayList<Piece> clonedPieces = new ArrayList();
        for (Piece piece : pieces) clonedPieces.add((Piece) piece.clone());

        ArrayList<Move> clonedHistory = new ArrayList();
        for (Move move : moveHistory) clonedHistory.add((Move) move.clone());

        clonedGame.moveHistory = clonedHistory;
        clonedGame.castlingConditionsWhite = copyArrayOfBools(castlingConditionsWhite);
        clonedGame.castlingConditionsBlack = copyArrayOfBools(castlingConditionsBlack);
        clonedGame.enPassantConditionsWhite = copyArrayOfBools(enPassantConditionsWhite);
        clonedGame.enPassantConditionsBlack = copyArrayOfBools(enPassantConditionsBlack);
        clonedGame.pieces = clonedPieces;
        clonedGame.buildBoard();

        return clonedGame;
    }


    @Override
    public int compareTo(Object o) {
        Game otherGame = (Game) o;

        int otherGameCheckState = otherGame.isKingChecked() ? 1 : -1;
        int otherGameState = otherGame.getState() != GameState.NEUTRAL ? 1 : -1;
        int boardCheckState = this.isKingChecked() ? -1 : 1;
        int boardState = this.getState() != GameState.NEUTRAL ? -1 : 1;

        return boardState + boardCheckState + otherGameState + otherGameCheckState;
    }

    //copys an array of bools (conditions) for the clone function
    public boolean[] copyArrayOfBools(boolean[] targetArray) {
        boolean[] copiedArray = new boolean[targetArray.length];
        for (int i = 0; i < targetArray.length; i++) {
            copiedArray[i] = targetArray[i];
        }
        return copiedArray;
    }

    public boolean[] getEnPassantConditionsWhite() {
        return enPassantConditionsWhite;
    }

    public boolean[] getEnPassantConditionsBlack() {
        return enPassantConditionsBlack;
    }

    public int getFiftyMoveClock() {
        return fiftyMoveClock;
    }

    public boolean[] getCastlingConditionsWhite() {
        return castlingConditionsWhite;
    }

    public boolean[] getCastlingConditionsBlack() {
        return castlingConditionsBlack;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public int getSeed(){
        return seed;
    }

    public GameState getState() {
        return state;
    }

    public int getOrientation() {
        return orientation;
    }

    public int[] getGraveyard() {
        return graveyard;
    }

    public Color getTurn() {
        return turn;
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public ArrayList<Piece> getPiecesByColor(Color color) {
        return pieces.stream()
                .filter(piece -> piece.getColor() == color)
                .collect(toCollection(ArrayList::new));
    }

    public Piece getPieceByTile(int x, int y) {
        for (Piece piece : pieces) {
            if (piece.getX() == x && piece.getY() == y) {
                return piece;
            }
        }
        return null;
    }

    public Piece getPieceByClone(Piece clonedPiece) {
        for (Piece piece : pieces) {
            if (piece.equals(clonedPiece)) {
                return piece;
            }
        }
        return null;
    }

    public Move getMoveByClone(Move clonedMove) {
        Piece piece = getPieceByClone(clonedMove.getPiece());
        return new Move(piece, clonedMove.getX(), clonedMove.getY());
    }

    public Piece getPieceByName(PieceName pieceName, Color color) {
        for (Piece piece : pieces) {
            if (piece.getPieceName() == pieceName &&
                    piece.getColor() == color) {
                return piece;
            }
        }
        return null;
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }

    public ArrayList<Piece> getUneatenPieces() {
        return pieces.stream()
                .filter(piece -> piece.getX() != graveyard[0] && piece.getY() != graveyard[1])
                .collect(toCollection(ArrayList::new));
    }

    public ArrayList<Piece> getEatenPieces() {
        return pieces.stream()
                .filter(piece -> piece.getX() == graveyard[0] && piece.getY() == graveyard[1])
                .collect(toCollection(ArrayList::new));
    }

    public ArrayList<Piece> getUneatenPiecesByColor(Color color) {
        return pieces.stream()
                .filter(piece -> piece.getX() != graveyard[0] && piece.getY() != graveyard[1] && piece.getColor() == color)
                .collect(toCollection(ArrayList::new));
    }

    public ArrayList<Move> getMoveHistory() {
        return moveHistory;
    }

    public Move getLastMove() {
        return moveHistory.get(moveHistory.size() - 1);
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public void setPieces(ArrayList<Piece> pieces) {
        this.pieces = pieces;
    }

    public void setTurn(Color turn) {
        this.turn = turn;
    }

    public void setTurnCounter(int turnCounter) {
        this.turnCounter = turnCounter;
    }

    public void setFiftyMoveClock(int fiftyMoveClock) {
        this.fiftyMoveClock = fiftyMoveClock;
    }

    public void setBoard(Piece[][] board) {
        this.board = board;
    }

    public void setCastlingConditionsWhite(boolean[] castlingConditionsWhite) {
        this.castlingConditionsWhite = castlingConditionsWhite;
    }

    public void setCastlingConditionsBlack(boolean[] castlingConditionsBlack) {
        this.castlingConditionsBlack = castlingConditionsBlack;
    }

    public void setEnPassantConditionsWhite(boolean[] enPassantConditionsWhite) {
        this.enPassantConditionsWhite = enPassantConditionsWhite;
    }

    public void setEnPassantConditionsBlack(boolean[] enPassantConditionsBlack) {
        this.enPassantConditionsBlack = enPassantConditionsBlack;
    }

    //adds last move to appropriate array to track threefold repetitions
    public void addLastMove(Move move) {
        Move archivedMove = (Move) move.clone();
        moveHistory.add(archivedMove);
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

    public void increaseTurnCount() {
        turnCounter++;
    }

    /**
     * the following methods are used by the scoring function in node class
     */

    public int castlingValue(Color targetColor){
        int value = 0;
        int castleState = targetColor == Color.WHITE ? whiteCastleState : blackCastleState;
        boolean[] castlingConditions = targetColor == Color.WHITE ? castlingConditionsWhite : castlingConditionsBlack;
        if(castleState == 0){
            for(boolean condition : castlingConditions){
                if(condition == true){
                    value++;
                }
            }
        }else{
            value = 3;
        }
        return value;
    }

    //evaluates the protection of the king for a given color
    public int kingProtectionValue(Color targetColor) {
        Piece king = getPieceByName(PieceName.KING, targetColor);
        int kingProtectionValue = 0;
        int kingX = king.getX();
        int kingY = king.getY();

        //go through surrounding pieces
        for (int i = kingX - 1; i <= kingX + 1; i++) {
            for (int j = kingY - 1; j <= kingY + 1; j++) {
                if (i > 7 || i < 0 || j > 7 || j < 0) {
                    kingProtectionValue++;
                } else if (getPieceByTile(i, j) != null && getPieceByTile(i, j).getColor() == targetColor) {
                    kingProtectionValue++;
                }
            }
        }

        return kingProtectionValue;
    }

    //evaluates whether you have the right colored bishop to fight against opponents castling.
    public int bishopValue(Color targetColor) {
        int targetCastleState = targetColor == Color.WHITE ? blackCastleState : whiteCastleState;
        if (targetCastleState != 0) {
            Color bishopTileColor = !(!(orientation == 1 ^ targetColor == Color.WHITE) ^ targetCastleState == 1) ? Color.BLACK : Color.WHITE;
            for (Piece piece : getUneatenPiecesByColor(targetColor)) {
                int tileColorToInt = bishopTileColor == Color.WHITE ? 0 : -1;
                if (piece.getPieceName() == PieceName.BISHOP && (piece.getX() + piece.getY()) % 2 == tileColorToInt) {
                    return 1;
                }
            }
        }
        return 0;
    }


    /**
     * The following methods are useful for the AI
     */
    public ArrayList<Game> generateLegalGamesByColor(Color color) {
        ArrayList<Move> legalMoves = this.generateLegalMovesByColor(color);
        ArrayList<Game> legalGames = new ArrayList<>();
        for (Move legalMove : legalMoves) {
            Game clonedGame = (Game) this.clone();
            Piece clonedPiece = clonedGame.getPieceByClone(legalMove.getPiece());
            clonedGame.movePiece(new Move(clonedPiece, legalMove.getX(), legalMove.getY()));
            legalGames.add(clonedGame);
        }
        return legalGames;
    }

    public ArrayList<Move> generateLegalMovesByColor(Color color) {
        ArrayList<Move> legalMovesList = new ArrayList<>();
        for (Piece piece : getUneatenPiecesByColor(color)) {
            legalMovesList.addAll(piece.generateMoves(this));
            //legalMovesList.addAll(getLegalMovesByPiece(piece));
        }
        return legalMovesList;
    }

    public ArrayList<Move> getLegalMovesByPiece(Piece piece) {
        ArrayList<Move> legalMovesList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Move move = new Move(piece, i, j);
                if (isMoveLegal(move)) {
                    legalMovesList.add(move);
                }
            }
        }
        return legalMovesList;
    }

    public float getGameValueByColor(Color color) {
        float value = 0;
        for (Piece piece : getUneatenPieces()) {
            if (piece.getColor() == color) {
                value += piece.getValue(orientation);
            } else {
                value -= piece.getValue(orientation);
            }
        }
        return value;
    }

    public float getSubsetValueByColor(Color color, ArrayList<Piece> pieces) {
        float value = 0;
        for (Piece piece : pieces) {
            if (piece.getColor() == color) {
                value += piece.getValue(orientation);
            } else {
                value -= piece.getValue(orientation);
            }
        }
        return value;
    }


    /**
     * End game detection.
     */
    public boolean isGameOver() {
        if (isGameInCheckMate()) {
            return true;
        } else if (isGameInStaleMate()) {
            return true;
        }
        return false;
    }

    public boolean isGameInCheckMate() {
        Piece king = getPieceByName(PieceName.KING, turn);
        if (!isPieceThreatened(king)) {
            return false;
        }
        for (Piece piece : getUneatenPieces()) {
            if (canPieceMove(piece)) {
                return false;
            }
        }
        setState(GameState.CHECKMATE);
        return true;
    }

    public boolean isGameInStaleMate() {
        if (noLegalMovesCheck()) {
            setState(GameState.STALEMATE);
            //System.out.println("no legal moves");
            return true;
        }
        if (insufficientMaterialCheck()) {
            setState(GameState.STALEMATE);
            //System.out.println("insufficient material");
            return true;
        }
        if (threefoldRepetitionCheck()) {
            setState(GameState.STALEMATE);
            //System.out.println("threefold repetition");
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
        for (Piece piece : getUneatenPiecesByColor(turn)) {
            if (canPieceMove(piece)) {
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
        if(piece.generateMoves(this).isEmpty()){
            return false;
        }
        return true;
    }


    public void buildBoard(){
        for(Piece piece: pieces){
            if(piece.getX() != -1){
                board[piece.getX()][piece.getY()] = piece;
            }
        }
    }

    /**
     * Manage moving pieces (any kind of movement)
     */
    public void movePiece(Move move) {
        Piece piece = move.getPiece();
        int destinationX = move.getX();
        int destinationY = move.getY();

        fiftyMoveClock++;
        Piece targetPiece = getPieceByTile(destinationX, destinationY);
        setState(GameState.NEUTRAL);
        updateEnPassantConditions(piece.getColor());

        if(piece.getPieceName() == PieceName.PAWN || targetPiece != null){ //reset 50 move clock if rules are met
            fiftyMoveClock = 0;
        }

        if (piece.getPieceName() == PieceName.PAWN && isPawnPromotionLegal(move)) {
            setState(GameState.PROMOTING_PAWN);
        }
        if (targetPiece != null) {//if a piece is to be eaten
            if(targetPiece.getPieceName() == PieceName.ROOK){//cant castle on that side if piece is eaten
                updateCastlingConditions(targetPiece);
            }
            targetPiece.discardPiece();
            setState(GameState.PIECE_EATEN);
            if (piece.getPieceName() == PieceName.PAWN && isPawnPromotionLegal(move)) {//is eating while promoting?
                setState(GameState.PROMOTING_AND_EATING);
            }
        }
        if (piece.getPieceName() == PieceName.PAWN && isPawnMove2Legal(move)) {//is this piece a pawn moving up 2?
            boolean[] targetConditions = piece.getColor() == Color.WHITE ? enPassantConditionsWhite : enPassantConditionsBlack;
            targetConditions[piece.getX()] = true;
        }
        if (piece.getPieceName() == PieceName.PAWN && isPawnEnPassantLegal(move)) {//is en passant legal?
            getPieceByTile(destinationX, piece.getY()).discardPiece();
            setState(GameState.EN_PASSANT);
        }
        if (piece.getPieceName() == PieceName.KING && isCastlingLegal(move)) {//is the piece a king and castling?
            castlingMove(move);
        }
        updateCastlingConditions(piece);
        addLastMove(move);
        piece.setTile(destinationX, destinationY);
        toggleTurn();
        buildBoard();
    }

    public void castlingMove(Move move) {
        Piece piece = move.getPiece();
        int destinationX = move.getX();
        int rookX;

        int rookXMove;
        int pieceX = piece.getX();
        int pieceY = piece.getY();
        int signedXMove = destinationX - pieceX;


        if(orientation == 1){
            rookXMove = signedXMove < 0 ? 2 : -3; //short if true
        }else{
            rookXMove = signedXMove > 0 ? -2 : 3; //short if true
        }

        rookX = signedXMove < 0 ? 0 : 7;

        Piece rook = this.getPieceByTile(rookX, pieceY);
        rook.setTile(rookX + rookXMove, pieceY);
        setState(Math.abs(rookXMove) == 2 ? GameState.CASTLING_SHORT : GameState.CASTLING_LONG);
        if (getTurn() == Color.WHITE) {
            whiteCastleState = Math.abs(rookXMove) == 2 ? 1 : 2;
        } else {
            blackCastleState = Math.abs(rookXMove) == 2 ? 1 : 2;
        }
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
            if (piece.getX() == 0) {
                castlingConditions[0] = false;
            } else {
                castlingConditions[2] = false;
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


    /**
     * Tests to decide if a move is legal
     */
    public boolean isMoveLegal(Move move) {


        /*
        The next few condition apply to any piece
         */

        Piece piece = move.getPiece();
        int destinationX = move.getX();
        int destinationY = move.getY();

        if (piece.getColor() != turn) {//is it the colors turn to move?
            return false;
        }
        Piece targetPiece = getPieceByTile(destinationX, destinationY);
        if (targetPiece != null) {
            if (targetPiece.getColor() == piece.getColor()) { //is it trying to eat its own color?
                return false;
            }
            if (targetPiece.getPieceName() == PieceName.KING) { //is it trying to eat a king?
                return false;
            }
        }
        if (willKingBeChecked(move)) {//checks if a move will cause its own king to be checked.
            return false;
        }
        /*
        Switch checks if the piece is behaving according to its type.
         */
        switch (piece.getPieceName()) {
            case PAWN:
                return isPawnMoveLegal(move);
            case KNIGHT:
                return isKnightMoveLegal(move);
            case BISHOP:
                return isBishopMoveLegal(move);
            case ROOK:
                return isRookMoveLegal(move);
            case KING:
                return isKingMoveLegal(move) || isCastlingLegal(move);
            case QUEEN:
                return isQueenMoveLegal(move);
            default:
                return false;
        }
    }

    public boolean isKnightMoveLegal(Move move) {
        Piece piece = move.getPiece();
        int x = piece.getX();
        int y = piece.getY();
        int signedYMove = move.getY() - y;
        int signedXMove = move.getX() - x;
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

    public boolean isBishopMoveLegal(Move move) {
        Piece piece = move.getPiece();
        int x = piece.getX();
        int y = piece.getY();
        int signedYMove = move.getY() - y;
        int signedXMove = move.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        if (xMove != yMove) {
            return false;
        }

        if (isPieceInTheWay(move)) {
            return false;
        }
        return true;
    }

    public boolean isRookMoveLegal(Move move) {
        Piece piece = move.getPiece();
        int x = piece.getX();
        int y = piece.getY();
        int signedYMove = move.getY() - y;
        int signedXMove = move.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        if (!(xMove == 0 || yMove == 0)) {
            return false;
        }

        if (isPieceInTheWay(move)) {
            return false;
        }
        return true;
    }

    public boolean isKingMoveLegal(Move move) {
        Piece piece = move.getPiece();
        int x = piece.getX();
        int y = piece.getY();
        int signedYMove = move.getY() - y;
        int signedXMove = move.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        if (isRookMoveLegal(move)) {
            if (yMove + xMove != 1) {
                return false;
            }
        } else if (isBishopMoveLegal(move)) {
            if (yMove + xMove != 2) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean isQueenMoveLegal(Move move) {
        if (isRookMoveLegal(move)) {
            return true;
        } else if (isBishopMoveLegal(move)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isPawnMoveLegal(Move move) {
        if (!isPawnOrientationLegal(move)) {
            return false;
        }
        if (isPawnMove1Legal(move) || isPawnMove2Legal(move) || isPawnEatLegal(move) || isPawnEnPassantLegal(move)) {
            return true;
        } else {
            return false;
        }
    }

    public int pawnOrientationByColor(Color color){
        if (orientation == 1) {
            if (color == Color.BLACK) {
                return -1;
            } else {
                return 1;
            }
        } else {
            if (color == Color.WHITE) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    //checks if pawn is moving in the right direction
    public boolean isPawnOrientationLegal(Move move) {
        Piece piece = move.getPiece();
        int x = piece.getX();
        int y = piece.getY();
        int signedYMove = move.getY() - y;
        int signedXMove = move.getX() - x;

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
    public boolean isPawnMove1Legal(Move move) {
        Piece piece = move.getPiece();
        int x = piece.getX();
        int y = piece.getY();
        int signedYMove = move.getY() - y;
        int signedXMove = move.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        for (Piece boardPiece : pieces) { //is piece in front of pawn
            if (boardPiece.getX() == move.getX() && boardPiece.getY() == move.getY()) {
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
    public boolean isPawnMove2Legal(Move move) {
        Piece piece = move.getPiece();
        int x = piece.getX();
        int y = piece.getY();
        int signedYMove = move.getY() - y;
        int signedXMove = move.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        for (Piece uneatenPiece : getUneatenPieces()) {
            if (uneatenPiece.getX() == move.getX() && uneatenPiece.getY() == move.getY()) {
                return false;
            }
        }

        if (xMove != 0) {
            return false;
        }
        if (!isRookMoveLegal(move)) {
            return false;
        }
        if (orientation == 1) {
            if (piece.getColor() == Color.WHITE) {
                if (piece.getY() != 1) {
                    return false;
                }
            } else {
                if (piece.getY() != 6) {
                    return false;
                }
            }
        } else {
            if (piece.getColor() == Color.BLACK) {
                if (piece.getY() != 1) {
                    return false;
                }
            } else {
                if (piece.getY() != 6) {
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
    public boolean isPawnEatLegal(Move move) {
        Piece piece = move.getPiece();
        int x = piece.getX();
        int y = piece.getY();
        int signedYMove = move.getY() - y;
        int signedXMove = move.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        if (isBishopMoveLegal(move)) {
            if (xMove != 1 || yMove != 1) {
                return false;
            }
        } else {
            return false;
        }
        Piece target = getPieceByTile(move.getX(), move.getY());
        if (target == null) {
            return false;
        } else if (target.getColor() == piece.getColor()) {
            return false;
        }

        return true;
    }

    //checks if en passant is legal (not implemented yet)
    public boolean isPawnEnPassantLegal(Move move) {
        Piece piece = move.getPiece();
        int x = piece.getX();
        int y = piece.getY();
        int signedYMove = move.getY() - y;
        int signedXMove = move.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        int expectedY = !(orientation == 1 ^ piece.getColor() == Color.WHITE) ? 4 : 3; //orientation 1 xnor white
        boolean[] targetConditions = piece.getColor() == Color.WHITE ? enPassantConditionsBlack : enPassantConditionsWhite;
        boolean targetCondition = targetConditions[move.getX()];
        Piece targetPiece = getPieceByTile(move.getX(), y);

        if (getPieceByTile(move.getX(), move.getY()) != null) { //is there a piece at the destination tile
            return false;
        }
        if (xMove != 1 || yMove != 1) { //is the move 1 square in each direction
            return false;
        }
        if (!targetCondition) { //has the target pawn moved 2 squares last turn
            return false;
        }
        if (expectedY != y) { //is the piece at its expected y position
            return false;
        }
        if (targetPiece == null) {
            return false;
        } else if (targetPiece.getPieceName() != PieceName.PAWN) {
            return false;
        }

        return true;
    }

    public boolean isPawnPromotionLegal(Move move) {
        if (!isPawnOrientationLegal(move)) {
            return false;
        }
        if (move.getY() != 7 && move.getY() != 0) {
            return false;
        }
        return true;
    }

    //checks if king is allowed to castle
    public boolean isCastlingLegal(Move move) {
        Piece piece = move.getPiece();
        int x = piece.getX();
        int y = piece.getY();
        int signedYMove = move.getY() - y;
        int signedXMove = move.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        boolean[] casltingConditions = piece.getColor() == Color.WHITE ? castlingConditionsWhite : castlingConditionsBlack;
        boolean kingCondition = casltingConditions[1];
        boolean castleCondition;
        int castleX;

        castleCondition = signedXMove < 0 ? casltingConditions[0] : casltingConditions[2];
        castleX = signedXMove < 0 ? 0 : 7;

        if (yMove != 0) {
            return false;
        }
        if (xMove != 2) {//is the king trying to move more than 2 squares
            return false;
        }
        if (!kingCondition || !castleCondition) {//has the king or the tower moved
            return false;
        }
        if (getPieceByTile(castleX, y) == null) {
            return false;
        }
        if (isPieceInTheWay(move)) {//is a piece between the king and the rook
            return false;
        }
        if (isPieceThreatened(piece)) { //is king checked
            return false;
        }
        if (willKingBeChecked(new Move(piece, signedXMove / 2 + x, y))) {//will the king get checked in between movements.
            return false;
        }
        return true;
    }

    //checks if a piece is in the trajectory (line or cross)
    public boolean isPieceInTheWay(Move move) {
        Piece piece = move.getPiece();
        int deltaX = move.getX() - piece.getX();
        int deltaY = move.getY() - piece.getY();
        int delta = Math.abs(deltaX) > Math.abs(deltaY) ? deltaX : deltaY;
        for (int i = 1; i < Math.abs(delta); i++) {
            int y = piece.getY() + i * (int) Math.signum(deltaY);
            int x = piece.getX() + i * (int) Math.signum(deltaX);
            if (getPieceByTile(x, y) != null) {
                return true;
            }
        }
        return false;
    }

    //checks if a piece is threatened
    public boolean isPieceThreatened(Piece piece) {
        Color color = piece.getColor() == Color.WHITE ? Color.BLACK : Color.WHITE;
        for (Piece enemyPiece : getUneatenPiecesByColor(color)) {
            Move move = new Move(enemyPiece, piece.getX(), piece.getY());
            if (enemyPiece.getPieceName() == PieceName.QUEEN && isQueenMoveLegal(move)) {
                return true;
            }
            if (enemyPiece.getPieceName() == PieceName.BISHOP && isBishopMoveLegal(move)) {
                return true;
            }
            if (enemyPiece.getPieceName() == PieceName.ROOK && isRookMoveLegal(move)) {
                return true;
            }
            if (enemyPiece.getPieceName() == PieceName.KNIGHT && isKnightMoveLegal(move)) {
                return true;
            }
            if (enemyPiece.getPieceName() == PieceName.KING && isKingMoveLegal(move)) {
                return true;
            }
            if (enemyPiece.getPieceName() == PieceName.PAWN && isPawnEatLegal(move) && isPawnOrientationLegal(move)) {
                return true;
            }
        }
        return false;
    }

    public boolean isKingChecked() {
        Piece king = this.getPieceByName(PieceName.KING, turn);
        if (this.isPieceThreatened(king)) {
            return true;
        }
        return false;
    }

    //checks if a move will cause its own king to be checked.
    public boolean willKingBeChecked(Move move) {
        Game clonedGame = (Game) this.clone();
        Move clonedMove = clonedGame.getMoveByClone(move);
        Piece clonedPiece = clonedMove.getPiece();
        if(move.getX() == -1){
            clonedPiece.discardPiece();
        }else{
            clonedGame.movePiece(clonedMove);
        }
        Piece clonedKing = clonedGame.getPieceByName(PieceName.KING, clonedPiece.getColor());
        return clonedGame.isPieceThreatened(clonedKing);
    }


    /**
     * Initializers
     */

    public void initializePieces() {
        int whiteY = orientation == 1 ? 0 : 7;
        int blackY = orientation == 1 ? 7 : 0;
        int queenX = orientation == 1 ? 3 : 4;
        int kingX = orientation == 1 ? 4 : 3;
        pieces = new ArrayList<Piece>();
        for (int i = 0; i < 8; i++) {
            int pawnY = orientation == 1 ? 1 : 6;
            pieces.add(new Piece(Color.WHITE, PieceName.PAWN, i, pawnY));
        }
        for (int i = 0; i < 8; i++) {
            int pawnY = orientation == 1 ? 6 : 1;
            pieces.add(new Piece(Color.BLACK, PieceName.PAWN, i, pawnY));
        }
        pieces.add(new Piece(Color.WHITE, PieceName.ROOK, 0, whiteY));
        pieces.add(new Piece(Color.WHITE, PieceName.KNIGHT, 1, whiteY));
        pieces.add(new Piece(Color.WHITE, PieceName.BISHOP, 2, whiteY));
        pieces.add(new Piece(Color.WHITE, PieceName.KING, kingX, whiteY));
        pieces.add(new Piece(Color.WHITE, PieceName.QUEEN, queenX, whiteY));
        pieces.add(new Piece(Color.WHITE, PieceName.BISHOP, 5, whiteY));
        pieces.add(new Piece(Color.WHITE, PieceName.KNIGHT, 6, whiteY));
        pieces.add(new Piece(Color.WHITE, PieceName.ROOK, 7, whiteY));

        pieces.add(new Piece(Color.BLACK, PieceName.ROOK, 0, blackY));
        pieces.add(new Piece(Color.BLACK, PieceName.KNIGHT, 1, blackY));
        pieces.add(new Piece(Color.BLACK, PieceName.BISHOP, 2, blackY));
        pieces.add(new Piece(Color.BLACK, PieceName.KING, kingX, blackY));
        pieces.add(new Piece(Color.BLACK, PieceName.QUEEN, queenX, blackY));
        pieces.add(new Piece(Color.BLACK, PieceName.BISHOP, 5, blackY));
        pieces.add(new Piece(Color.BLACK, PieceName.KNIGHT, 6, blackY));
        pieces.add(new Piece(Color.BLACK, PieceName.ROOK, 7, blackY));
    }

    public void initializePromotingTest() {
        pieces = new ArrayList<Piece>();
        pieces.add(new Piece(Color.WHITE, PieceName.KING, 3, 0));
        pieces.add(new Piece(Color.BLACK, PieceName.KING, 3, 7));
        pieces.add(new Piece(Color.WHITE, PieceName.PAWN, 0, 1));
        pieces.add(new Piece(Color.BLACK, PieceName.PAWN, 7, 6));
    }

    public void initializeStalemateTest() {
        pieces = new ArrayList<Piece>();
        pieces.add(new Piece(Color.WHITE, PieceName.KING, 0, 7));
        pieces.add(new Piece(Color.BLACK, PieceName.KING, 7, 0));
        pieces.add(new Piece(Color.WHITE, PieceName.QUEEN, 4, 1));
    }

    public void initializeinsufficientMatTest() {
        pieces = new ArrayList<Piece>();
        pieces.add(new Piece(Color.WHITE, PieceName.KING, 0, 7));
        pieces.add(new Piece(Color.BLACK, PieceName.KING, 7, 0));
        pieces.add(new Piece(Color.WHITE, PieceName.PAWN, 1, 6));
        pieces.add(new Piece(Color.WHITE, PieceName.QUEEN, graveyard[0], graveyard[1]));
    }

    public void initializeCheckMateTest() {
        pieces = new ArrayList<Piece>();
        pieces.add(new Piece(Color.WHITE, PieceName.KING, 0, 7));
        pieces.add(new Piece(Color.BLACK, PieceName.KING, 7, 0));
        pieces.add(new Piece(Color.WHITE, PieceName.QUEEN, 4, 1));
        pieces.add(new Piece(Color.WHITE, PieceName.ROOK, 6, 2));
    }
}

