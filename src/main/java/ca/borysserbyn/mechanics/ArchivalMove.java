package ca.borysserbyn.mechanics;

public class ArchivalMove extends Move{
    private String boardSnapShot;

    public ArchivalMove(Piece piece, int x, int y) {
        super(piece, x, y);
    }

    public ArchivalMove(Piece piece, int x, int y, GameState stateSnapShot, PieceName promotionSnapShot) {
        super(piece, x, y, stateSnapShot, promotionSnapShot);
    }

    public static ArchivalMove archiveMove(Move move, Piece[][] board){
        ArchivalMove archivalMove = new ArchivalMove((Piece) move.piece.clone(), move.x, move.y, move.stateSnapShot, move.promotionSnapShot);
        archivalMove.boardSnapShot = NotationUtils.createFenFromBoard(board);
        return archivalMove;
    }
}
