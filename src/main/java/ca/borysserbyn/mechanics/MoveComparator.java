package ca.borysserbyn.mechanics;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class MoveComparator implements Comparator<Move> {

    private Game game;

    public MoveComparator(Game game) {
        this.game = game;
    }

    @Override
    public int compare(Move move1, Move move2) {
        return (int) Math.signum(scoreMove(move2) - scoreMove(move1));
    }

    public float scoreMove(Move move){
        float value = 0.f;
        Piece targetPiece = game.getPieceByTile(move.getX(), move.getY());
        if(targetPiece != null){
            value = 10*targetPiece.getValue(game.getOrientation()) - move.getPiece().getValue(game.getOrientation());
        }
        return value;
    }

    @Override
    public Comparator<Move> reversed() {
        return null;
    }

    @Override
    public Comparator<Move> thenComparing(Comparator<? super Move> other) {
        return null;
    }

    @Override
    public <U> Comparator<Move> thenComparing(Function<? super Move, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
        return null;
    }

    @Override
    public <U extends Comparable<? super U>> Comparator<Move> thenComparing(Function<? super Move, ? extends U> keyExtractor) {
        return null;
    }

    @Override
    public Comparator<Move> thenComparingInt(ToIntFunction<? super Move> keyExtractor) {
        return null;
    }

    @Override
    public Comparator<Move> thenComparingLong(ToLongFunction<? super Move> keyExtractor) {
        return null;
    }

    @Override
    public Comparator<Move> thenComparingDouble(ToDoubleFunction<? super Move> keyExtractor) {
        return null;
    }
}
