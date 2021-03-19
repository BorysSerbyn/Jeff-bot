package ca.borysserbyn.mechanics;

public enum GameState {
    NEUTRAL,
    PIECE_EATEN,
    EN_PASSANT,
    CASTLING_SHORT,
    CASTLING_LONG,
    PROMOTING_PAWN,
    PROMOTING_AND_EATING,
    STALEMATE,
    CHECKMATE,
    UNDEFINED
}
