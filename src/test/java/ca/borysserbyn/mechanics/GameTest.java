package ca.borysserbyn.mechanics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class GameTest {
    @Test
    void generateLegalMovesByColorTest() {
        String fenStr = "3k4/3p4/8/8/8/8/3P4/3K4 w - - 0 1";
        ArrayList<Move> expectedMovesList = new ArrayList<>();
        Game game = NotationUtils.createGameFromFen(fenStr);
        ArrayList<Move> moveList = game.generateLegalMovesByColor(Color.WHITE);
        Assertions.assertEquals(expectedMovesList, moveList);
    }

    @Test
    void threeFoldRepetitionTest1() {
        Game game = FileUtils.readGameByPath("saved_games/threeFoldTest.xml");
        Piece blackQueen = game.getPieceByTile(3, 7);
        Piece whiteQueen = game.getPieceByTile(3, 0);

        game.movePiece(new Move(whiteQueen, 3, 1));
        game.movePiece(new Move(blackQueen, 3, 6));

        game.movePiece(new Move(whiteQueen, 3, 0));
        game.movePiece(new Move(blackQueen, 3, 7));

        game.movePiece(new Move(whiteQueen, 3, 1));
        game.movePiece(new Move(blackQueen, 3, 6));

        game.movePiece(new Move(whiteQueen, 3, 2));

        game.isGameOver();

        Assertions.assertEquals(GameState.NEUTRAL, game.getState());
    }

    @Test
    void threeFoldRepetitionTest2() {
        Game game = FileUtils.readGameByPath("saved_games/threeFoldTest.xml");
        Piece whiteQueen = game.getPieceByTile(3, 0);
        Piece blackQueen = game.getPieceByTile(3, 7);

        game.movePiece(new Move(whiteQueen, 3, 1));
        game.movePiece(new Move(blackQueen, 3, 6));

        game.movePiece(new Move(whiteQueen, 3, 0));
        game.movePiece(new Move(blackQueen, 3, 7));

        game.movePiece(new Move(whiteQueen, 3, 1));
        game.movePiece(new Move(blackQueen, 3, 6));

        game.movePiece(new Move(whiteQueen, 3, 0));
        game.movePiece(new Move(blackQueen, 3, 7));


        game.isGameOver();

        Assertions.assertEquals(GameState.STALEMATE, game.getState());
    }

    @Test
    void threeFoldRepetitionTest3() {
        Game game = new Game(1);
        Piece whiteHorse = game.getPieceByTile(6, 7);
        Piece blackHorse = game.getPieceByTile(6, 0);

        game.movePiece(new Move(whiteHorse, 5, 2));
        game.movePiece(new Move(blackHorse, 5, 5));

        game.movePiece(new Move(whiteHorse, 6, 7));
        game.movePiece(new Move(blackHorse, 6, 0));

        game.movePiece(new Move(whiteHorse, 5, 2));
        game.movePiece(new Move(blackHorse, 5, 5));

        game.movePiece(new Move(whiteHorse, 6, 7));

        game.isGameOver();

        Assertions.assertEquals(GameState.NEUTRAL, game.getState());
    }

    @Test
    void castlingValueTest() {
        String fenStr = "4k2r/8/8/8/8/8/8/4K3 b k - 0 1";
        Game game = NotationUtils.createGameFromFen(fenStr);
        game.setBlackCastleState(1);
        float actualValue = game.castlingValue(Color.BLACK) - game.castlingValue(Color.WHITE);
        Assertions.assertEquals(3.0f, actualValue);
    }
}
