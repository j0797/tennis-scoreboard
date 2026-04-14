package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.model.MatchScore;
import com.example.tennisscoreboard.model.OngoingMatch;

public class MatchScoreCalculationService {

    private static final int POINTS_DEUCE_THRESHOLD = 3;
    private static final int GAMES_TO_WIN_SET = 6;
    private static final int MIN_GAMES_DIFFERENCE_FOR_SET = 2;
    private static final int TIEBREAK_TRIGGER_GAMES = 6;
    private static final int TIEBREAK_POINTS_TO_WIN = 7;
    private static final int TIEBREAK_MIN_DIFFERENCE = 2;
    private static final int SETS_TO_WIN_MATCH = 2;

    public static void addPoint(OngoingMatch match, int playerNumber) {
        if (match.isMatchOver()) return;

        MatchScore score = match.getScore();
        boolean isPlayerOne = (playerNumber == 1);

        if (match.isTieBreak()) {
            handleTieBreakPoint(match, isPlayerOne);
        } else {
            handleRegularPoint(match, isPlayerOne);
        }
    }

    private static void handleRegularPoint(OngoingMatch match, boolean isPlayerOne) {

        MatchScore score = match.getScore();
        int scoringPoints = isPlayerOne ? score.getPlayerOnePoints() : score.getPlayerTwoPoints();
        int opponentPoints = isPlayerOne ? score.getPlayerTwoPoints() : score.getPlayerOnePoints();

        if (scoringPoints == POINTS_DEUCE_THRESHOLD) {
            if (opponentPoints == POINTS_DEUCE_THRESHOLD) {
                match.setDeuce(true);
                handleDeuce(match, isPlayerOne);
            } else {
                winGame(match, isPlayerOne);
            }
        } else {
            if (isPlayerOne) score.setPlayerOnePoints(scoringPoints + 1);
            else score.setPlayerTwoPoints(scoringPoints + 1);
        }
    }

    private static void handleDeuce(OngoingMatch match, boolean isPlayerOne) {
        MatchScore score = match.getScore();
        if (isPlayerOne) {
            if (score.isPlayerOneAdvantage()) {
                resetAdvantage(score);
                winGame(match, true);
                match.setDeuce(false);
            } else if (score.isPlayerTwoAdvantage()) {
                resetAdvantage(score);
            } else {
                score.setPlayerOneAdvantage(true);
            }
        } else {
            if (score.isPlayerTwoAdvantage()) {
                resetAdvantage(score);
                winGame(match, false);
                match.setDeuce(false);
            } else if (score.isPlayerOneAdvantage()) {
                resetAdvantage(score);
            } else {
                score.setPlayerTwoAdvantage(true);
            }
        }
    }

    private static void winGame(OngoingMatch match, boolean isPlayerOne) {
        MatchScore score = match.getScore();
        score.setPlayerOnePoints(0);
        score.setPlayerTwoPoints(0);
        resetAdvantage(score);
        match.setDeuce(false);

        if (isPlayerOne) score.setPlayerOneGames(score.getPlayerOneGames() + 1);
        else score.setPlayerTwoGames(score.getPlayerTwoGames() + 1);

        checkSetWinner(match);
    }

    private static void checkSetWinner(OngoingMatch match) {
        MatchScore score = match.getScore();
        int p1Games = score.getPlayerOneGames();
        int p2Games = score.getPlayerTwoGames();

        if (p1Games >= GAMES_TO_WIN_SET && (p1Games - p2Games) >= MIN_GAMES_DIFFERENCE_FOR_SET) {
            winSet(match, true);
        } else if (p2Games >= GAMES_TO_WIN_SET && (p2Games - p1Games) >= MIN_GAMES_DIFFERENCE_FOR_SET) {
            winSet(match, false);
        } else if (p1Games == TIEBREAK_TRIGGER_GAMES && p2Games == TIEBREAK_TRIGGER_GAMES) {
            match.setTieBreak(true);
        }
    }

    private static void winSet(OngoingMatch match, boolean isPlayerOne) {
        MatchScore score = match.getScore();
        if (isPlayerOne) score.setPlayerOneSets(score.getPlayerOneSets() + 1);
        else score.setPlayerTwoSets(score.getPlayerTwoSets() + 1);

        score.setPlayerOneGames(0);
        score.setPlayerTwoGames(0);
        score.setPlayerOneTieBreakPoints(0);
        score.setPlayerTwoTieBreakPoints(0);
        match.setTieBreak(false);

        if (score.getPlayerOneSets() == SETS_TO_WIN_MATCH || score.getPlayerTwoSets() == SETS_TO_WIN_MATCH) {
            match.setMatchOver(true);
        }
    }

    private static void handleTieBreakPoint(OngoingMatch match, boolean isPlayerOne) {
        MatchScore score = match.getScore();
        if (isPlayerOne) score.setPlayerOneTieBreakPoints(score.getPlayerOneTieBreakPoints() + 1);
        else score.setPlayerTwoTieBreakPoints(score.getPlayerTwoTieBreakPoints() + 1);

        int scoringTB = isPlayerOne ? score.getPlayerOneTieBreakPoints() : score.getPlayerTwoTieBreakPoints();
        int opponentTB = isPlayerOne ? score.getPlayerTwoTieBreakPoints() : score.getPlayerOneTieBreakPoints();

        if (scoringTB >= TIEBREAK_POINTS_TO_WIN && (scoringTB - opponentTB) >= TIEBREAK_MIN_DIFFERENCE) {
            winSet(match, isPlayerOne);
        }
    }

    private static void resetAdvantage(MatchScore score) {
        score.setPlayerOneAdvantage(false);
        score.setPlayerTwoAdvantage(false);
    }
}
