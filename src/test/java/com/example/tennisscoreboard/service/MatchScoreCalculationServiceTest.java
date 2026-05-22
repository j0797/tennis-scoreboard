package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.entity.Player;
import com.example.tennisscoreboard.model.OngoingMatch;
import com.example.tennisscoreboard.service.impl.MatchScoreCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchScoreCalculationServiceTest {

    // После проведение декомпозиции и рефакторинга доменных моделей, также следует изменить тесты для этой части логики.

    private OngoingMatch match;

    @BeforeEach
    void setUp() {

        // В тестах доменных моделей (как и в самих моделях) не должны использоваться JPA Entity
        Player p1 = new Player("PlayerOne");
        Player p2 = new Player("PlayerTwo");
        match = new OngoingMatch(p1, p2);
    }

    @Test
    void playerOneScoresFromLoveTo15() {
        MatchScoreCalculationService.addPoint(match, 1);
        assertEquals(1, match.getScore().getPlayerOnePoints());
        assertEquals(0, match.getScore().getPlayerTwoPoints());
        assertFalse(match.isTieBreak());
        assertFalse(match.isMatchOver());
    }

    @Test
    void playerOneScoresFrom15To30() {
        match.getScore().setPlayerOnePoints(1);
        MatchScoreCalculationService.addPoint(match, 1);
        assertEquals(2, match.getScore().getPlayerOnePoints());
    }

    @Test
    void playerOneScoresFrom30To40() {
        match.getScore().setPlayerOnePoints(2);
        MatchScoreCalculationService.addPoint(match, 1);
        assertEquals(3, match.getScore().getPlayerOnePoints());
    }

    @Test
    void playerWinsGameWhenScoringFrom40ToLove() {
        match.getScore().setPlayerOnePoints(3);
        match.getScore().setPlayerTwoPoints(0);
        MatchScoreCalculationService.addPoint(match, 1);
        assertEquals(0, match.getScore().getPlayerOnePoints());
        assertEquals(0, match.getScore().getPlayerTwoPoints());
        assertEquals(1, match.getScore().getPlayerOneGames());
        assertEquals(0, match.getScore().getPlayerTwoGames());
        assertFalse(match.isTieBreak());
    }

    @Test
    void playerWinsGameWhenScoringFrom40_30() {
        match.getScore().setPlayerOnePoints(3);
        match.getScore().setPlayerTwoPoints(2);
        MatchScoreCalculationService.addPoint(match, 1);
        assertEquals(1, match.getScore().getPlayerOneGames());
        assertEquals(0, match.getScore().getPlayerTwoGames());
    }

    @Test
    void deuceWhenBothAt40() {
        match.getScore().setPlayerOnePoints(3);
        match.getScore().setPlayerTwoPoints(3);
        MatchScoreCalculationService.addPoint(match, 1);
        assertTrue(match.getScore().isPlayerOneAdvantage());
        assertFalse(match.getScore().isPlayerTwoAdvantage());
        assertEquals(3, match.getScore().getPlayerOnePoints());
        assertEquals(3, match.getScore().getPlayerTwoPoints());
        assertEquals(0, match.getScore().getPlayerOneGames());
        assertTrue(match.isDeuce());
    }

    @Test
    void advantageWinsGameWhenScoringAgain() {
        match.getScore().setPlayerOnePoints(3);
        match.getScore().setPlayerTwoPoints(3);
        MatchScoreCalculationService.addPoint(match, 1);
        assertTrue(match.getScore().isPlayerOneAdvantage());
        MatchScoreCalculationService.addPoint(match, 1);
        assertEquals(1, match.getScore().getPlayerOneGames());
        assertEquals(0, match.getScore().getPlayerTwoGames());
        assertFalse(match.getScore().isPlayerOneAdvantage());
        assertFalse(match.getScore().isPlayerTwoAdvantage());
        assertFalse(match.isDeuce());
    }

    @Test
    void advantageLeadsToGameWin() {
        match.getScore().setPlayerOnePoints(3);
        match.getScore().setPlayerTwoPoints(3);
        match.getScore().setPlayerOneAdvantage(true);
        MatchScoreCalculationService.addPoint(match, 1);
        assertEquals(1, match.getScore().getPlayerOneGames());
        assertEquals(0, match.getScore().getPlayerTwoGames());
        assertFalse(match.getScore().isPlayerOneAdvantage());
        assertFalse(match.getScore().isPlayerTwoAdvantage());
    }

    @Test
    void advantageLostWhenOpponentScores() {
        match.getScore().setPlayerOnePoints(3);
        match.getScore().setPlayerTwoPoints(3);
        MatchScoreCalculationService.addPoint(match, 1);
        MatchScoreCalculationService.addPoint(match, 2);
        assertFalse(match.getScore().isPlayerOneAdvantage());
        assertFalse(match.getScore().isPlayerTwoAdvantage());
        assertTrue(match.isDeuce());
        assertEquals(3, match.getScore().getPlayerOnePoints());
        assertEquals(3, match.getScore().getPlayerTwoPoints());
    }

    @Test
    void playerWinsSetAt6_4() {
        match.getScore().setPlayerOneGames(5);
        match.getScore().setPlayerTwoGames(4);
        match.getScore().setPlayerOnePoints(3);
        match.getScore().setPlayerTwoPoints(0);
        MatchScoreCalculationService.addPoint(match, 1);
        assertEquals(1, match.getScore().getPlayerOneSets());
        assertEquals(0, match.getScore().getPlayerTwoSets());
        assertEquals(0, match.getScore().getPlayerOneGames());
        assertEquals(0, match.getScore().getPlayerTwoGames());
        assertFalse(match.isTieBreak());
    }

    @Test
    void tieBreakStartsAt6_6() {
        match.getScore().setPlayerOneGames(6);
        match.getScore().setPlayerTwoGames(6);
        match.getScore().setPlayerOnePoints(3);
        match.getScore().setPlayerTwoPoints(0);
        MatchScoreCalculationService.addPoint(match, 1);
        assertTrue(match.isTieBreak());
        assertEquals(6, match.getScore().getPlayerOneGames());
        assertEquals(6, match.getScore().getPlayerTwoGames());
    }

    @Test
    void tieBreakWinAt7_5() {
        match.setTieBreak(true);
        match.getScore().setPlayerOneTieBreakPoints(6);
        match.getScore().setPlayerTwoTieBreakPoints(5);
        MatchScoreCalculationService.addPoint(match, 1);
        assertEquals(1, match.getScore().getPlayerOneSets());
        assertEquals(0, match.getScore().getPlayerTwoSets());
        assertFalse(match.isTieBreak());
        assertEquals(0, match.getScore().getPlayerOneTieBreakPoints());
        assertEquals(0, match.getScore().getPlayerTwoTieBreakPoints());
    }

    @Test
    void tieBreakContinuesAt6_6() {
        match.setTieBreak(true);
        match.getScore().setPlayerOneTieBreakPoints(6);
        match.getScore().setPlayerTwoTieBreakPoints(6);
        MatchScoreCalculationService.addPoint(match, 1);
        assertEquals(7, match.getScore().getPlayerOneTieBreakPoints());
        assertEquals(6, match.getScore().getPlayerTwoTieBreakPoints());
        assertEquals(0, match.getScore().getPlayerOneSets());
        assertTrue(match.isTieBreak());
    }

    @Test
    void matchEndsWhenPlayerWinsTwoSets() {
        match.getScore().setPlayerOneSets(1);
        match.getScore().setPlayerOneGames(5);
        match.getScore().setPlayerTwoGames(4);
        match.getScore().setPlayerOnePoints(3);
        match.getScore().setPlayerTwoPoints(0);
        MatchScoreCalculationService.addPoint(match, 1);
        assertTrue(match.isMatchOver());
        assertEquals(2, match.getScore().getPlayerOneSets());
        assertEquals(0, match.getScore().getPlayerTwoSets());
    }
}