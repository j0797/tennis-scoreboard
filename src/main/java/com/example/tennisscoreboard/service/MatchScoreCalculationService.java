package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.model.MatchScore;
import com.example.tennisscoreboard.model.OngoingMatch;

public class MatchScoreCalculationService {

    public static void addPoint(OngoingMatch match, int playerNumber) {
        if (match.isMatchOver()) return;

        MatchScore score = match.getScore();
        boolean isPlayerOne = (playerNumber == 1);

        if (match.isOpenTieBreak()) {
            handleTieBreakPoint(match, isPlayerOne);
        } else {
            handleRegularPoint(match, isPlayerOne);
        }
    }

    private static void handleRegularPoint(OngoingMatch match, boolean isPlayerOne) {

        MatchScore score = match.getScore();
        int scoringPoints = isPlayerOne ? score.getPlayerOnePoints() : score.getPlayerTwoPoints();
        int opponentPoints = isPlayerOne ? score.getPlayerTwoPoints() : score.getPlayerOnePoints();

        if (scoringPoints == 3) {
            if (opponentPoints == 3) {
                match.setDeuceSituation(true);
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
                match.setDeuceSituation(false);
            } else if (score.isPlayerTwoAdvantage()) {
                resetAdvantage(score);
            } else {
                score.setPlayerOneAdvantage(true);
            }
        } else {
            if (score.isPlayerTwoAdvantage()) {
                resetAdvantage(score);
                winGame(match, false);
                match.setDeuceSituation(false);
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
        match.setDeuceSituation(false);

        if (isPlayerOne) score.setPlayerOneGames(score.getPlayerOneGames() + 1);
        else score.setPlayerTwoGames(score.getPlayerTwoGames() + 1);

        checkSetWinner(match);
    }

    private static void checkSetWinner(OngoingMatch match) {
        MatchScore score = match.getScore();
        int p1Games = score.getPlayerOneGames();
        int p2Games = score.getPlayerTwoGames();

        if (p1Games >= 6 && (p1Games - p2Games) >= 2) {
            winSet(match, true);
        } else if (p2Games >= 6 && (p2Games - p1Games) >= 2) {
            winSet(match, false);
        } else if (p1Games == 6 && p2Games == 6) {
            match.setOpenTieBreak(true);
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
        match.setOpenTieBreak(false);

        if (score.getPlayerOneSets() == 2 || score.getPlayerTwoSets() == 2) {
            match.setMatchOver(true);
        }
    }

    private static void handleTieBreakPoint(OngoingMatch match, boolean isPlayerOne) {
        MatchScore score = match.getScore();
        if (isPlayerOne) score.setPlayerOneTieBreakPoints(score.getPlayerOneTieBreakPoints() + 1);
        else score.setPlayerTwoTieBreakPoints(score.getPlayerTwoTieBreakPoints() + 1);

        int scoringTB = isPlayerOne ? score.getPlayerOneTieBreakPoints() : score.getPlayerTwoTieBreakPoints();
        int opponentTB = isPlayerOne ? score.getPlayerTwoTieBreakPoints() : score.getPlayerOneTieBreakPoints();

        if (scoringTB >= 7 && (scoringTB - opponentTB) >= 2) {
            winSet(match, isPlayerOne);
        }
    }

    private static void resetAdvantage(MatchScore score) {
        score.setPlayerOneAdvantage(false);
        score.setPlayerTwoAdvantage(false);
    }
}
