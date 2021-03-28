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

    @Test
    void kingProtectionValueTest() {
        String fenStr = "rnbq1rk1/pppppp1p/7p/8/8/5N2/PPPPPPPP/RNBQ1RK1 w Qq - 0 1";
        Game game = NotationUtils.createGameFromFen(fenStr);
        game.setBlackCastleState(1);
        game.setWhiteCastleState(1);
        float actualValue = game.kingProtectionByColor(Color.WHITE) - game.kingProtectionByColor(Color.BLACK);
        float expectedValue = (float) ((1 + 1.5 + 0.5 + 0.5) - (1 - 1.5 -0.5 + 0.5));
        Assertions.assertEquals(expectedValue, actualValue);
    }

    @Test
    void kingProtectionValueTest2() {
        String fenStr = "2kr1bnr/pppppppp/2n5/8/4P3/1P1P4/P1P2PPP/1K1R1BNR w Kk - 0 1";
        Game game = NotationUtils.createGameFromFen(fenStr);
        game.setBlackCastleState(1);
        game.setWhiteCastleState(1);
        float actualValue = game.kingProtectionByColor(Color.WHITE) - game.kingProtectionByColor(Color.BLACK);
        float expectedValue = (float) ((1 + 1.5 + 0.5 + 0.5) - (1 - 1.5 -0.5 + 0.5));
        Assertions.assertEquals(expectedValue, actualValue);
    }

    @Test
    void gameScoreTestCastlingShort() {
        String fenStr = "rnbq1rk1/pppppp1p/7p/8/8/5N2/PPPPPPPP/RNBQ1RK1 w Qq - 0 1";
        Game badgame = NotationUtils.createGameFromFen("rn1q1rk1/pppppp1p/7p/8/8/4PP2/PPPP1P1P/RNBQ1RK1 w Qq - 0 1");
        Game goodGame = NotationUtils.createGameFromFen("rn1q1rk1/pppppp1p/7p/8/8/4PQ2/PPPP1PPP/RNB2RK1 w Qq - 0 1");

        badgame.setBlackCastleState(1);
        badgame.setWhiteCastleState(1);
        goodGame.setBlackCastleState(1);
        goodGame.setWhiteCastleState(1);

        float badScore = badgame.scorePosition(Color.WHITE);
        float goodScore = goodGame.scorePosition(Color.WHITE);
        Assertions.assertTrue(badScore < goodScore);
    }

    @Test
    void gameScoreTestCastlingLong() {
        String fenStr = "2kr1bnr/pppppppp/2n5/8/4PN2/1P1P4/P1P2PPP/1K1R1BNR w Kk - 0 1";
        Game game = NotationUtils.createGameFromFen(fenStr);

        game.setBlackCastleState(2);
        game.setWhiteCastleState(2);

        float whiteScore = game.scorePosition(Color.WHITE);
        float blackScore = game.scorePosition(Color.BLACK);

        Assertions.assertTrue(whiteScore < blackScore);
    }

    @Test
    void bitBoardMoveTest() {
        String fenStr = "7k/8/8/8/p7/p7/1P6/7K w - - 0 1";
        Game game = NotationUtils.createGameFromFen(fenStr);

        Piece whitePawn = game.getPieceByTile(1, 1);
        Piece blackPawn = game.getPieceByTile(0, 2);
        Piece blackPawn2 = game.getPieceByTile(0, 3);

        game.movePiece(new Move(whitePawn, 1, 2));

        long expectedValue = 0b100000000000000000;

        Assertions.assertEquals(expectedValue, game.getBitBoardArray()[0]);
    }

    @Test
    void bitBoardMoveTest2() {
        String fenStr = "7k/8/8/8/p7/p7/1P6/7K w - - 0 1";
        Game game = NotationUtils.createGameFromFen(fenStr);

        Piece whitePawn = game.getPieceByTile(1, 1);
        Piece blackPawn = game.getPieceByTile(0, 2);
        Piece blackPawn2 = game.getPieceByTile(0, 3);

        game.movePiece(new Move(whitePawn, 0, 2));

        long expectedValue = 0b1000000000000000000000000;

        Assertions.assertEquals(expectedValue, game.getBitBoardArray()[6]);
    }
}
