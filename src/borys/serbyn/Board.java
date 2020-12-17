package borys.serbyn;

import java.awt.*;
import java.util.ArrayList;

public class Board implements Cloneable{
    private ArrayList<Piece> pieces;
    private final int orientation; //1 if white is up
    private Color turn;
    private int turnCounter;
    private Tile[][] tiles;
    private Tile graveyard;
    private boolean[] castlingConditionsWhite;
    private boolean[] castlingConditionsBlack;
    private boolean[] enPassantConditionsWhite;
    private boolean[] enPassantConditionsBlack;
    private BoardState state;


    public Board(int orientation) {
        this.state = BoardState.NEUTRAL;
        this.turnCounter = 0;
        this.orientation = orientation;
        this.graveyard = new Tile(-1,-1, Color.WHITE);
        this.turn = Color.WHITE;
        initializeTiles();
        initializePieces();
        //initializePromotingTest();

        this.castlingConditionsWhite = new boolean[] {true,true,true};
        this.castlingConditionsBlack = new boolean[] {true,true,true};
        this.enPassantConditionsWhite = new boolean[] {false,false,false,false,false,false,false,false};
        this.enPassantConditionsBlack = new boolean[] {false,false,false,false,false,false,false,false};
    }

    public Board(ArrayList<Piece> pieces, int orientation, Color turn, int turnCounter, Tile[][] tiles, Tile graveyard, boolean[] castlingConditionsWhite, boolean[] castlingConditionsBlack, boolean[] enPassantConditionsWhite, boolean[] enPassantConditionsBlack, BoardState state) {
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
        this.state = state;
    }

    @Override
    public Object clone() {
        Board board = null;
        try {
            board = (Board) super.clone();
        } catch (CloneNotSupportedException e) {
            board = new Board(pieces, orientation, turn, turnCounter, tiles, graveyard, castlingConditionsWhite, castlingConditionsBlack, enPassantConditionsWhite,
                    enPassantConditionsBlack, state);
        }


        Tile[][] clonedTiles = new Tile[8][8];
        ArrayList<Piece> clonedPieces = new ArrayList<Piece>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                clonedTiles[i][j] = (Tile) board.getTileByPosition(i,j).clone();
            }
        }

        for(Piece piece: board.getPieces()){
            Piece clonedPiece = (Piece) piece.clone();
            Tile clonedTile = clonedPiece.getTile();
            if(clonedTile.getX() != -1){ //check if the piece isnt in the graveyard
                clonedTiles[clonedTile.getX()][clonedTile.getY()] = clonedTile;
            }
            clonedPieces.add(clonedPiece);
        }
        board.castlingConditionsWhite = copyArrayOfBools(castlingConditionsWhite);
        board.castlingConditionsBlack = copyArrayOfBools(castlingConditionsBlack);
        board.enPassantConditionsWhite = copyArrayOfBools(enPassantConditionsWhite);
        board.enPassantConditionsBlack = copyArrayOfBools(enPassantConditionsBlack);
        board.pieces = clonedPieces;
        board.tiles = clonedTiles;
        board.graveyard = (Tile) this.graveyard.clone();

        return board;
    }

    //copys an array of bools (conditions) for the clone function
    public boolean[] copyArrayOfBools(boolean[] targetArray){
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

    public void toggleTurn() {
        increaseTurnCount();
        if(turn == Color.BLACK){
            for (boolean cond:enPassantConditionsWhite) {
                cond = false;
            }
            turn = Color.WHITE;
        }else{
            for (boolean cond:enPassantConditionsBlack) {
                cond = false;
            }
            turn = Color.BLACK;
        }
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public void increaseTurnCount(){
        turnCounter++;
    }

    public Piece getPieceByTile(Tile tile){
        for(Piece piece:pieces){
            if(piece.getTile() == tile){
                return piece;
            }
        }

        return null;
    }

    public Piece getPieceByName(PieceName pieceName, Color color){
        for(Piece piece:pieces){
            if(piece.getPieceName() == pieceName && piece.getColor() == color){
                return piece;
            }
        }
        return null;
    }

    public Tile getTileByPosition(int x, int y){
        return tiles[x][y];
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }


    /*
    Manage moving pieces (any kind of movement)
     */
    public void movePiece(Piece piece, Tile tile){
        Piece targetPiece = getPieceByTile(tile);
        setState(BoardState.NEUTRAL);

        if(targetPiece != null){//if a piece is to be eaten
            targetPiece.discardPiece(graveyard);
            setState(BoardState.PIECE_EATEN);
            if(piece.getPieceName() == PieceName.PAWN && isPawnPromotionLegal(piece,tile)){
                setState(BoardState.PROMOTING_AND_EATING);
            }
        }
        if(piece.getPieceName() == PieceName.PAWN && isPawnEnPassantLegal(piece,tile)){//is this piece a pawn moving up 2?
            boolean[] targetConditions = piece.getColor() == Color.WHITE ? enPassantConditionsWhite : enPassantConditionsBlack;
            targetConditions[piece.getTile().getX()] = true;
            getPieceByTile(getTileByPosition(tile.getX(), piece.getTile().getY())).discardPiece(graveyard);
            setState(BoardState.EN_PASSANT);
        }
        if(piece.getPieceName() == PieceName.PAWN && isPawnPromotionLegal(piece,tile)){
            setState(BoardState.PROMOTING_PAWN);
        }
        if(piece.getPieceName() == PieceName.KING && isCastlingLegal(piece, tile) ){//is the piece a king and castling?
           castlingMove(piece, tile);
        }
        updateCastlingConditions(piece);

        piece.setTile(tile);
        toggleTurn();
    }

    public void castlingMove(Piece piece, Tile tile){
        Tile rookTile;
        int rookXMove;
        int x = piece.getTile().getX();
        int signedXMove = tile.getX() - x;

        if(orientation == 1){
            rookXMove = signedXMove < 0  ? 2 : -3; //short if true
            rookTile = signedXMove < 0 ? getTileByPosition(0, piece.getTile().getY()) : getTileByPosition(7, piece.getTile().getY());
        }else{
            rookXMove = signedXMove > 0  ? -2 : 3; //short if true
            rookTile = signedXMove > 0 ? getTileByPosition(0, piece.getTile().getY()) : getTileByPosition(7, piece.getTile().getY());
        }
        Piece rook = this.getPieceByTile(rookTile);
        rook.setTile(getTileByPosition(rookTile.getX()+rookXMove, rookTile.getY()));
        setState(Math.abs(rookXMove) == 2 ? BoardState.CASTLING_SHORT : BoardState.CASTLING_LONG);
    }

    //updates castling condition based on the movement of a given piece (did rooks or king move)
    public void updateCastlingConditions(Piece piece){
        boolean[] castlingConditions = piece.getColor() == Color.WHITE ? castlingConditionsWhite : castlingConditionsBlack;

        if(piece.getPieceName() == PieceName.KING){//turns off castling conditions if king moves.
            for (int i = 0; i < 3; i++) {
                castlingConditions[i] = false;
            }
        }

        if(piece.getPieceName() == PieceName.ROOK){//turns off castling condition for a specific rook if it moves.
            if(orientation == 1){
                if(piece.getTile().getX() == 0){
                    castlingConditions[0] = false;
                }else{
                    castlingConditions[2] = false;
                }
            }else{
                if(piece.getTile().getX() == 7){
                    castlingConditions[0] = false;
                }else{
                    castlingConditions[2] = false;
                }
            }
        }
    }

    public void promotingPawn(Piece piece, PieceName pieceName){
        piece.setPieceName(pieceName);
    }


    /*
    Tests to decide if a move is legal
     */
    public boolean isMoveLegal(Piece piece, Tile tile){
        /*
        The next few condition apply to any piece
         */
        if(piece.getColor() != turn){//is it the colors turn to move?
            return false;
        }
        for(Piece boardPiece: pieces){//is the piece trying to eat its own color? (includes himself)
            if(boardPiece.getTile() == tile && boardPiece.getColor() == piece.getColor()){
                return false;
            }
        }
        if(willKingBeChecked(piece,tile)){//checks if a move will cause its own king to be checked.
            return false;
        }
        /*
        Switch checks if the piece is behaving according to its type.
         */
        switch(piece.getPieceName()){
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

    public boolean isKnightMoveLegal(Piece piece, Tile tile){
        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int signedXMove = tile.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        if(xMove != 2 && xMove != 1){
            return false;
        }

        if(xMove + yMove != 3){
            return false;
        }

        return true;
    }

    public boolean isBishopMoveLegal(Piece piece, Tile tile){
        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int signedXMove = tile.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        if(xMove != yMove){
            return false;
        }

        if(isPieceInTheWay(piece, tile)){
            return false;
        }
        return true;
    }

    public boolean isRookMoveLegal(Piece piece, Tile tile){
        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int signedXMove = tile.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        if(!(xMove == 0 || yMove == 0)){
            return false;
        }

        if(isPieceInTheWay(piece, tile)){
            return false;
        }
        return true;
    }

    public boolean isKingMoveLegal(Piece piece, Tile tile){
        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int signedXMove = tile.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        if(isRookMoveLegal(piece, tile)){
            if(yMove+xMove != 1){
                return false;
            }
        }else if(isBishopMoveLegal(piece,tile)){
            if(yMove+xMove != 2){
                return false;
            }
        }else{
            return false;
        }
        return true;
    }

    public boolean isQueenMoveLegal(Piece piece, Tile tile){
        if(isRookMoveLegal(piece, tile)){
            return true;
        }else if(isBishopMoveLegal(piece,tile)){
            return true;
        }else{
            return false;
        }
    }

    public boolean isPawnMoveLegal(Piece piece, Tile tile){
        if(!isPawnOrientationLegal(piece, tile)){
            return false;
        }
        if(isPawnMove1Legal(piece, tile) || isPawnMove2Legal(piece, tile) || isPawnEatLegal(piece, tile) || isPawnEnPassantLegal(piece, tile)){
            return true;
        }else{
            return false;
        }
    }

    //checks if pawn is moving in the right direction
    public boolean isPawnOrientationLegal(Piece piece, Tile tile){
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int yMove = Math.abs(signedYMove);

        if(orientation == 1) {
            if (piece.getColor() == Color.BLACK) {
                if(signedYMove > 0){
                    return false;
                }
            }else {
                if(signedYMove < 0){
                    return false;
                }
            }
        }else{
            if (piece.getColor() == Color.WHITE) {
                if(signedYMove > 0){
                    return false;
                }
            }else {
                if(signedYMove < 0){
                    return false;
                }
            }
        }

        return true;
    }

    //checks if the pawn is moving 1 square
    public boolean isPawnMove1Legal(Piece piece, Tile tile){
        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int signedXMove = tile.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        for(Piece boardPiece: pieces){
            if(boardPiece.getTile() == tile){
                return false;
            }
        }
        if(xMove != 0){ // does pawn move diagonaly
            return false;
        }
        if(yMove != 1){
            return false;
        }
        return true;
    }

    //checks if the pawn is moving 2 squares as its first move.
    public boolean isPawnMove2Legal(Piece piece, Tile tile){
        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int signedXMove = tile.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        if(xMove != 0){
            return false;
        }
        if(orientation == 1){
            if(piece.getColor() == Color.WHITE){
                if(piece.getTile().getY() != 1){
                    return false;
                }
            }else{
                if(piece.getTile().getY() != 6){
                    return false;
                }
            }
        }else{
            if(piece.getColor() == Color.BLACK){
                if(piece.getTile().getY() != 1){
                    return false;
                }
            }else{
                if(piece.getTile().getY() != 6){
                    return false;
                }
            }
        }

        if(yMove != 2){
            return false;
        }

        return true;
    }

    //checks if normal eat is legal
    public boolean isPawnEatLegal(Piece piece, Tile tile){
        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int signedXMove = tile.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        boolean isPieceThere = false;
        for(Piece boardPiece: pieces){
            if(boardPiece.getTile() == tile){
                isPieceThere = true;
            }
        }

        if(!isPieceThere){
            return false;
        }else{
            if(isBishopMoveLegal(piece,tile)) {
                if (xMove != 1 || yMove != 1) {
                    return false;
                }
            }else{
                return false;
            }
        }
        return true;
    }

    //checks if en passant is legal (not implemented yet)
    public boolean isPawnEnPassantLegal(Piece piece, Tile tile){
        int x = piece.getTile().getX();
        int y = piece.getTile().getY();
        int signedYMove = tile.getY() - y;
        int signedXMove = tile.getX() - x;
        int xMove = Math.abs(signedXMove);
        int yMove = Math.abs(signedYMove);

        Color targetColor = orientation == 1 ? Color.WHITE : Color.BLACK;
        boolean[] targetConditions = piece.getColor() == Color.WHITE ? enPassantConditionsBlack : enPassantConditionsWhite;

        if(getPieceByTile(tile) != null){
            return false;
        }

        if(isBishopMoveLegal(piece,tile)) {
            if (xMove != 1 || yMove != 1) {
                return false;
            }
        }else{
            return false;
        }

        if(piece.getColor() == targetColor){
            if(y == 4){
                if(getPieceByTile(getTileByPosition(tile.getX(), y)) == null){
                    if(!targetConditions[tile.getX()]){
                        return false;
                    }
                }
            }else{
                return false;
            }
        }else{
            if(y == 3){
                if(getPieceByTile(getTileByPosition(tile.getX(), y)) == null){
                    if(!targetConditions[tile.getX()]){
                        return false;
                    }
                }
            }else{
                return false;
            }
        }

        return true;
    }

    public boolean isPawnPromotionLegal(Piece piece, Tile tile){
        if(!isPawnOrientationLegal(piece, tile)){
            System.out.println("test");
            return false;
        }
        if(tile.getY() != 7 && tile.getY() != 0){
            return false;
        }
        return true;
    }

    //checks if king is allowed to castle
    public boolean isCastlingLegal(Piece piece, Tile tile){
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
        Tile inBetweenTile = getTileByPosition(signedXMove/2+x,y);

        if(orientation == 1){
            castleCondition = signedXMove < 0  ? casltingConditions[0] : casltingConditions[2]; //short if true
            castleTile = signedXMove < 0 ? getTileByPosition(0, piece.getTile().getY()) : getTileByPosition(7, piece.getTile().getY());
        }else{
            castleCondition = signedXMove > 0  ? casltingConditions[0] : casltingConditions[2];
            castleTile = signedXMove > 0 ? getTileByPosition(0, piece.getTile().getY()) : getTileByPosition(7, piece.getTile().getY());
        }

        if(isPieceThreatened(piece)){ //is king checked
            return false;
        }
        if(!kingCondition || !castleCondition){//has the king or the tower moved
            return false;
        }
        if(xMove != 2){//is the king trying to move more than 2 squares
            return false;
        }
        if(isPieceInTheWay(piece,castleTile)){//is a piece between the king and the rook
            return false;
        }

        if(willKingBeChecked(piece, inBetweenTile)){//will the king get checked in between movements.
            System.out.println("test5");
            return false;
        }
        return true;
    }

    //checks if a piece is in the trajectory (line or cross)
    public boolean isPieceInTheWay(Piece piece, Tile tile){
        int yMove = tile.getY() - piece.getTile().getY();
        int xMove = tile.getX() - piece.getTile().getX();
        int move = Math.abs(xMove) > Math.abs(yMove) ? xMove : yMove;

        for (int i = 1; i < Math.abs(move); i++) {
            int y = piece.getTile().getY() + i*(int)Math.signum(yMove);
            int x = piece.getTile().getX() + i*(int)Math.signum(xMove);
            Tile targetTile = getTileByPosition(x, y);
            if(getPieceByTile(targetTile) != null){
                return true;
            }
        }

        return false;
    }

    //checks if a piece is threatened
    public boolean isPieceThreatened(Piece piece){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Tile targetTile = getTileByPosition(i, j);
                if(isQueenMoveLegal(piece, targetTile)){
                    Piece targetPiece = getPieceByTile(targetTile);
                    if(targetPiece != null && targetPiece.getPieceName() == PieceName.QUEEN && targetPiece.getColor() != piece.getColor()){
                        System.out.println(piece.toString() + " is threatened by :" + targetPiece.toString());
                        return true;
                    }
                }
                if(isBishopMoveLegal(piece, targetTile)){
                    Piece targetPiece = getPieceByTile(targetTile);
                    if(targetPiece != null && targetPiece.getPieceName() == PieceName.BISHOP && targetPiece.getColor() != piece.getColor()){
                        System.out.println(piece.toString() + " is threatened by :" + targetPiece.toString());
                        return true;
                    }
                }
                if(isRookMoveLegal(piece, targetTile)){
                    if(targetTile.getY() == 7 && targetTile.getX() == 0){
                    }
                    Piece targetPiece = getPieceByTile(targetTile);
                    if(targetPiece != null && targetPiece.getPieceName() == PieceName.ROOK && targetPiece.getColor() != piece.getColor()){
                        System.out.println(piece.toString() + " is threatened by :" + targetPiece.toString());
                        return true;
                    }
                }
                if(isKnightMoveLegal(piece, targetTile)){
                    Piece targetPiece = getPieceByTile(targetTile);
                    if(targetPiece != null && targetPiece.getPieceName() == PieceName.KNIGHT && targetPiece.getColor() != piece.getColor()){
                        System.out.println(piece.toString() + " is threatened by :" + targetPiece.toString());
                        return true;
                    }
                }
                if(isKingMoveLegal(piece, targetTile)){
                    Piece targetPiece = getPieceByTile(targetTile);
                    if(targetPiece != null && targetPiece.getPieceName() == PieceName.KING && targetPiece.getColor() != piece.getColor()){
                        System.out.println(piece.toString() + " is threatened by :" + targetPiece.toString());
                        return true;
                    }
                }
                if(isPawnEatLegal(piece, targetTile) && isPawnOrientationLegal(piece, targetTile)){
                    Piece targetPiece = getPieceByTile(targetTile);
                    if(targetPiece != null && targetPiece.getPieceName() == PieceName.PAWN && targetPiece.getColor() != piece.getColor()){
                        System.out.println(piece.toString() + " is threatened by :" + targetPiece.toString());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //checks if a move will cause its own king to be checked.
    public boolean willKingBeChecked(Piece piece, Tile tile){
        Board boardDeepCopy = (Board) this.clone();
        Tile tileDeepCopy = boardDeepCopy.getTileByPosition(piece.getTile().getX(), piece.getTile().getY());
        Tile destinationTileDC = boardDeepCopy.getTileByPosition(tile.getX(), tile.getY());
        Piece pieceDeepCopy = boardDeepCopy.getPieceByTile(tileDeepCopy);
        boardDeepCopy.movePiece(pieceDeepCopy,destinationTileDC);
        Piece kingDeepCopy = boardDeepCopy.getPieceByName(PieceName.KING, piece.getColor());
        if(boardDeepCopy.isPieceThreatened(kingDeepCopy)){
            return true;
        }
        pieceDeepCopy = null;
        kingDeepCopy = null;
        tileDeepCopy = null;
        boardDeepCopy = null;
        return false;
    }


    /*
    Initializers
     */
    public void initializeTiles(){
        tiles = new Tile[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if((j+i)%2 == 0){
                    tiles[i][j] = new Tile(i, j, Color.WHITE);
                }else{
                    tiles[i][j] = new Tile(i, j, Color.BLACK);
                }
            }
        }
    }

    public void initializePieces(){
        pieces = new ArrayList<Piece>();
        for (int i = 0; i < 8; i++) {
            pieces.add(new Piece(Color.WHITE, PieceName.PAWN, getTileByPosition(i, 1)));
        }
        for (int i = 0; i < 8; i++) {
            pieces.add(new Piece(Color.BLACK, PieceName.PAWN, getTileByPosition(i, 6)));
        }
        pieces.add(new Piece(Color.WHITE, PieceName.ROOK, getTileByPosition(0,0)));
        pieces.add(new Piece(Color.WHITE, PieceName.KNIGHT, getTileByPosition(1,0)));
        pieces.add(new Piece(Color.WHITE, PieceName.BISHOP, getTileByPosition(2,0)));
        pieces.add(new Piece(Color.WHITE, PieceName.KING, getTileByPosition(3,0)));
        pieces.add(new Piece(Color.WHITE, PieceName.QUEEN, getTileByPosition(4,0)));
        pieces.add(new Piece(Color.WHITE, PieceName.BISHOP, getTileByPosition(5,0)));
        pieces.add(new Piece(Color.WHITE, PieceName.KNIGHT, getTileByPosition(6,0)));
        pieces.add(new Piece(Color.WHITE, PieceName.ROOK, getTileByPosition(7,0)));

        pieces.add(new Piece(Color.BLACK, PieceName.ROOK, getTileByPosition(0,7)));
        pieces.add(new Piece(Color.BLACK, PieceName.KNIGHT, getTileByPosition(1,7)));
        pieces.add(new Piece(Color.BLACK, PieceName.BISHOP, getTileByPosition(2,7)));
        pieces.add(new Piece(Color.BLACK, PieceName.KING, getTileByPosition(3,7)));
        pieces.add(new Piece(Color.BLACK, PieceName.QUEEN, getTileByPosition(4,7)));
        pieces.add(new Piece(Color.BLACK, PieceName.BISHOP, getTileByPosition(5,7)));
        pieces.add(new Piece(Color.BLACK, PieceName.KNIGHT, getTileByPosition(6,7)));
        pieces.add(new Piece(Color.BLACK, PieceName.ROOK, getTileByPosition(7,7)));
    }

    public void initializePromotingTest(){
        pieces = new ArrayList<Piece>();
        pieces.add(new Piece(Color.WHITE, PieceName.KING, getTileByPosition(3,0)));
        pieces.add(new Piece(Color.BLACK, PieceName.KING, getTileByPosition(3,7)));
        pieces.add(new Piece(Color.WHITE, PieceName.PAWN, getTileByPosition(0, 1)));
        pieces.add(new Piece(Color.BLACK, PieceName.PAWN, getTileByPosition(7, 6)));
    }
}

