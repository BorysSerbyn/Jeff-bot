package borys.serbyn;

public enum BoardState {
    NEUTRAL,
    PIECE_EATEN,
    EN_PASSANT,
    CASTLING_SHORT,
    CASTLING_LONG,
    PROMOTING_PAWN,
    PROMOTING_AND_EATING,
    STALE_MATE,
    CHECK_MATE,
    CHECK,
}
