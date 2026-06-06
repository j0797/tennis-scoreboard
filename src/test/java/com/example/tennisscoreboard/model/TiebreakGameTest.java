package com.example.tennisscoreboard.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TiebreakGameTest {

    private static final int PLAYER_ONE = 1;
    private static final int PLAYER_TWO = 2;
    private static final int POINTS_TO_WIN = 7;
    private static final int POINTS_BEFORE_TIEBREAK = POINTS_TO_WIN - 1;
    private static final int ADDITIONAL_POINTS_TO_WIN = 2;

    private TiebreakGame tiebreak;

    @BeforeEach
    void setUp() {
        tiebreak = new TiebreakGame();
    }

    @Test
    void shouldNotBeOverInitially() {
        assertFalse(tiebreak.isOver());
    }

    @Test
    void shouldFirstPlayerWinTiebreak() {
        scorePoints(PLAYER_ONE, POINTS_TO_WIN);
        assertTrue(tiebreak.isOver());
        assertEquals(PLAYER_ONE, tiebreak.winner());
    }

    @Test
    void shouldSecondPlayerWinTiebreak() {
        scorePoints(PLAYER_TWO, POINTS_TO_WIN);
        assertTrue(tiebreak.isOver());
        assertEquals(PLAYER_TWO, tiebreak.winner());
    }

    @Test
    void shouldNotEndAt6_6() {
        scorePoints(PLAYER_ONE, POINTS_BEFORE_TIEBREAK);
        scorePoints(PLAYER_TWO, POINTS_BEFORE_TIEBREAK);
        assertFalse(tiebreak.isOver());
    }

    @Test
    void shouldNotEndAt7_6() {
        scorePoints(PLAYER_ONE, POINTS_BEFORE_TIEBREAK);
        scorePoints(PLAYER_TWO, POINTS_BEFORE_TIEBREAK);
        assertFalse(tiebreak.isOver());
        tiebreak.scorePoint(PLAYER_ONE);
        assertFalse(tiebreak.isOver());
        tiebreak.scorePoint(PLAYER_TWO);
        assertFalse(tiebreak.isOver());
    }

    @Test
    void shouldEndAt8_6() {
        scorePoints(PLAYER_ONE, POINTS_BEFORE_TIEBREAK);
        scorePoints(PLAYER_TWO, POINTS_BEFORE_TIEBREAK);
        scorePoints(PLAYER_ONE, ADDITIONAL_POINTS_TO_WIN);
        assertTrue(tiebreak.isOver());
        assertEquals(PLAYER_ONE, tiebreak.winner());
    }


    @Test
    void shouldThrowWhenScoringInFinishedTiebreak() {
        scorePoints(PLAYER_ONE, POINTS_TO_WIN);
        assertThrows(IllegalStateException.class, () -> tiebreak.scorePoint(PLAYER_ONE));
    }

    @Test
    void shouldThrowWhenRequestingWinnerBeforeTiebreakIsOver() {
        assertThrows(IllegalStateException.class, tiebreak::winner);
    }

    private void scorePoints(int player, int count) {
        for (int i = 0; i < count; i++) {
            tiebreak.scorePoint(player);
        }
    }
}