package com.example.tennisscoreboard.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SetScoreTest {
    private static final int PLAYER_ONE = 1;
    private static final int PLAYER_TWO = 2;
    private static final int POINTS_TO_WIN_GAME = 4;
    private static final int GAMES_TO_WIN_SET = 6;
    private static final int TIEBREAK_POINTS_TO_WIN = 7;
    private static final int HALF_GAMES = GAMES_TO_WIN_SET - 1;
    private static final int TIEBREAK_WINNER_GAMES = GAMES_TO_WIN_SET + 1;
    private static final int TIEBREAK_LOSER_GAMES = GAMES_TO_WIN_SET;

    private SetScore set;

    @BeforeEach
    void setUp() {
        set = new SetScore();
    }

    @Test
    void shouldNotBeOverInitially() {
        assertFalse(set.isOver());
    }

    @Test
    void shouldFirstPlayerWinSet() {
        winGames(PLAYER_ONE, GAMES_TO_WIN_SET);
        assertTrue(set.isOver());
        assertEquals(PLAYER_ONE, set.winner());
    }

    @Test
    void shouldSecondPlayerWinSet() {
        winGames(PLAYER_TWO, GAMES_TO_WIN_SET);
        assertTrue(set.isOver());
        assertEquals(PLAYER_TWO, set.winner());
    }

    @Test
    void shouldNotEndSetAt5_5() {
        winGames(PLAYER_ONE, HALF_GAMES);
        winGames(PLAYER_TWO, HALF_GAMES);
        assertFalse(set.isOver());
    }

    @Test
    void shouldStartTiebreakAt6_6() {
        reachTiebreak();
        assertTrue(set.isTiebreak());
        assertFalse(set.isOver());
    }

    @Test
    void shouldFirstPlayerWinTiebreak() {
        reachTiebreak();
        for (int i = 0; i < TIEBREAK_POINTS_TO_WIN; i++) {
            set.scorePoint(PLAYER_ONE);
        }
        assertTrue(set.isOver());
        assertEquals(PLAYER_ONE, set.winner());
        assertEquals(TIEBREAK_WINNER_GAMES, set.getGamesOne());
        assertEquals(TIEBREAK_LOSER_GAMES, set.getGamesTwo());
    }

    @Test
    void shouldThrowWhenScoringInFinishedSet() {
        winGames(PLAYER_ONE, GAMES_TO_WIN_SET);
        assertThrows(IllegalStateException.class, () -> set.scorePoint(PLAYER_ONE));
    }

    @Test
    void shouldThrowWhenRequestingWinnerBeforeSetIsOver() {
        assertThrows(IllegalStateException.class, set::winner);
    }

    private void winGame(int player) {
        for (int i = 0; i < POINTS_TO_WIN_GAME; i++) {
            set.scorePoint(player);
        }
    }

    private void winGames(int player, int count) {
        for (int i = 0; i < count; i++) {
            winGame(player);
        }
    }

    private void reachTiebreak() {
        winGames(PLAYER_ONE, HALF_GAMES);
        winGames(PLAYER_TWO, HALF_GAMES);
        winGame(PLAYER_ONE);
        winGame(PLAYER_TWO);
    }
}