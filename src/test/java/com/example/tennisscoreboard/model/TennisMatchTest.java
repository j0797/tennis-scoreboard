package com.example.tennisscoreboard.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TennisMatchTest {
    private static final int PLAYER_ONE = 1;
    private static final int PLAYER_TWO = 2;
    private static final int GAMES_TO_WIN_SET = 6;
    private static final int SETS_TO_WIN_MATCH = 2;
    private static final int POINTS_TO_WIN_GAME = 4;

    private TennisMatch match;
    private final Player firstPlayer = new Player(1L, "Bob");
    private final Player secondPlayer = new Player(2L, "Alice");

    @BeforeEach
    void setUp() {
        match = new TennisMatch(firstPlayer, secondPlayer);
    }

    @Test
    void shouldNotBeOverInitially() {
        assertFalse(match.isOver());
    }

    @Test
    void shouldThrowWhenRequestingWinnerBeforeMatchIsOver() {
        assertThrows(IllegalStateException.class, match::winner);
    }

    @Test
    void shouldFirstPlayerWinMatch() {
        winSets(PLAYER_ONE);
        assertTrue(match.isOver());
        assertEquals(firstPlayer, match.winner());
    }

    @Test
    void shouldSecondPlayerWinMatch() {
        winSets(PLAYER_TWO);
        assertTrue(match.isOver());
        assertEquals(secondPlayer, match.winner());
    }

    @Test
    void shouldThrowWhenScoringInFinishedMatch() {
        winSets(PLAYER_ONE);
        assertThrows(IllegalStateException.class, () -> match.scorePoint(PLAYER_ONE));
    }

    private void winSets(int player) {
        for (int i = 0; i < SETS_TO_WIN_MATCH; i++) {
            winGames(player);
        }
    }

    private void winGames(int player) {
        for (int i = 0; i < GAMES_TO_WIN_SET; i++) {
            winGame(player);
        }
    }

    private void winGame(int player) {
        for (int i = 0; i < POINTS_TO_WIN_GAME; i++) {
            match.scorePoint(player);
        }
    }
}