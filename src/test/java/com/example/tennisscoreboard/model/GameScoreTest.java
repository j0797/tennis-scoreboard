package com.example.tennisscoreboard.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameScoreTest {

    private static final int PLAYER_ONE = 1;
    private static final int PLAYER_TWO = 2;
    private static final int POINTS_TO_WIN_WITHOUT_DEUCE = 4;
    private static final int POINTS_TO_DEUCE = 3;

    private GameScore game;

    @BeforeEach
    void setUp() {
        game = new GameScore();
    }

    @Test
    void shouldStartWithLoveScore() {
        assertEquals(Points.LOVE, game.getFirstPlayer());
        assertEquals(Points.LOVE, game.getSecondPlayer());
    }

    @Test
    void shouldFirstPlayerWinsGameWithoutDeuce() {
        scorePoints(PLAYER_ONE, POINTS_TO_WIN_WITHOUT_DEUCE);
        assertTrue(game.isOver());
        assertEquals(PLAYER_ONE, game.winner());
    }

    @Test
    void shouldSecondPlayerWinsGameWithoutDeuce() {
        scorePoints(PLAYER_TWO, POINTS_TO_WIN_WITHOUT_DEUCE);
        assertTrue(game.isOver());
        assertEquals(PLAYER_TWO, game.winner());
    }

    @Test
    void shouldGiveAdvantageAfterDeuce() {
        reachDeuce();
        game.scorePoint(PLAYER_ONE);
        assertEquals(Points.ADVANTAGE, game.getFirstPlayer());
        assertEquals(Points.FORTY, game.getSecondPlayer());
    }

    @Test
    void shouldReturnToDeuceWhenAdvantageLost() {
        reachDeuce();
        game.scorePoint(PLAYER_ONE);
        game.scorePoint(PLAYER_TWO);
        assertEquals(Points.FORTY, game.getFirstPlayer());
        assertEquals(Points.FORTY, game.getSecondPlayer());
    }

    @Test
    void shouldWinGameAfterAdvantage() {
        reachDeuce();
        game.scorePoint(PLAYER_ONE);
        game.scorePoint(PLAYER_ONE);
        assertTrue(game.isOver());
        assertEquals(PLAYER_ONE, game.winner());
    }

    @Test
    void shouldThrowWhenScoringInFinishedGame() {
        scorePoints(PLAYER_ONE, POINTS_TO_WIN_WITHOUT_DEUCE);
        assertThrows(IllegalStateException.class, () -> game.scorePoint(PLAYER_ONE));
    }

    private void reachDeuce() {
        scorePoints(PLAYER_ONE, POINTS_TO_DEUCE);
        scorePoints(PLAYER_TWO, POINTS_TO_DEUCE);
    }

    private void scorePoints(int player, int count) {
        for (int i = 0; i < count; i++) {
            game.scorePoint(player);
        }
    }
}