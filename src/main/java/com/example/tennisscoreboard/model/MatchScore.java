package com.example.tennisscoreboard.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchScore {

    private int playerOnePoints = 0;
    private int playerTwoPoints = 0;
    private int playerOneGames = 0;
    private int playerTwoGames = 0;
    private int playerOneSets = 0;
    private int playerTwoSets = 0;
    private int playerOneTieBreakPoints = 0;
    private int playerTwoTieBreakPoints = 0;
    private boolean playerOneAdvantage = false;
    private boolean playerTwoAdvantage = false;
    private boolean openTieBreak = false;
    private boolean deuceSituation = false;
    private boolean matchOver = false;


    public void addPointToPlayer(int playerNumber) {
        if (matchOver) return;

        boolean isPlayerOne = (playerNumber == 1);

        if (openTieBreak) {
            handleTieBreakPoint(isPlayerOne);
        } else {
            handleRegularPoint(isPlayerOne);
        }
    }

    private void handleRegularPoint(boolean isPlayerOne) {

        int scoringPoints = isPlayerOne ? playerOnePoints : playerTwoPoints;
        int opponentPoints = isPlayerOne ? playerTwoPoints : playerOnePoints;

        if (scoringPoints == 3) {
            if (opponentPoints == 3) {
                deuceSituation = true;
                handleDeuce(isPlayerOne);
            } else {
                winGame(isPlayerOne);
            }
        } else {
            if (isPlayerOne) playerOnePoints++;
            else playerTwoPoints++;
        }
    }

    private void handleDeuce(boolean isPlayerOne) {
        if (isPlayerOne) {
            if (playerOneAdvantage) {
                resetAdvantage();
                winGame(true);
                deuceSituation = false;
            } else if (playerTwoAdvantage) {
                resetAdvantage();
            } else {
                playerOneAdvantage = true;
            }
        } else {
            if (playerTwoAdvantage) {
                resetAdvantage();
                winGame(false);
                deuceSituation = false;
            } else if (playerOneAdvantage) {
                resetAdvantage();
            } else {
                playerTwoAdvantage = true;
            }
        }
    }

    private void winGame(boolean isPlayerOne) {
        playerOnePoints = 0;
        playerTwoPoints = 0;
        resetAdvantage();
        deuceSituation = false;

        if (isPlayerOne) playerOneGames++;
        else playerTwoGames++;
        checkSetWinner();
    }

    private void checkSetWinner() {
        if (playerOneGames >= 6 && (playerOneGames - playerTwoGames) >= 2) {
            winSet(true);
        } else if (playerTwoGames >= 6 && (playerTwoGames - playerOneGames) >= 2) {
            winSet(false);
        } else if (playerOneGames == 6 && playerTwoGames == 6) {
            openTieBreak = true;
        }
    }

    private void winSet(boolean isPlayerOne) {
        if (isPlayerOne) playerOneSets++;
        else playerTwoSets++;

        playerOneGames = 0;
        playerTwoGames = 0;
        playerOneTieBreakPoints = 0;
        playerTwoTieBreakPoints = 0;
        openTieBreak = false;

        if (playerOneSets == 2) {
            matchOver = true;
        } else if (playerTwoSets == 2) {
            matchOver = true;
        }
    }

    private void handleTieBreakPoint(boolean isPlayerOne) {
        if (isPlayerOne) playerOneTieBreakPoints++;
        else playerTwoTieBreakPoints++;

        int scoringTB = isPlayerOne ? playerOneTieBreakPoints : playerTwoTieBreakPoints;
        int opponentTB = isPlayerOne ? playerTwoTieBreakPoints : playerOneTieBreakPoints;

        if (scoringTB >= 7 && (scoringTB - opponentTB) >= 2) {
            winSet(isPlayerOne);
        }
    }

    private void resetAdvantage() {
        playerOneAdvantage = false;
        playerTwoAdvantage = false;
    }
}