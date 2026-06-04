package com.example.tennisscoreboard.model;

import lombok.Getter;

@Getter
public class SetScore {
    private static final int GAMES_TO_WIN = 6;
    private static final int MIN_DIFFERENCE = 2;
    private static final int TIEBREAK_TRIGGER = 6;
    private int gamesOne;
    private int gamesTwo;
    private GameScore currentGameScore;
    private TiebreakGame tiebreakGame;

    public SetScore() {
        this.currentGameScore = new GameScore();
    }

    public void scorePoint(int player) {
        if (isOver()) {
            throw new IllegalStateException("Cannot score a point in a finished set");
        }
        if (isTiebreak()) {
            tiebreakGame.scorePoint(player);
            if (tiebreakGame.isOver()) {
                applyTiebreakResult();
            }
        } else {
            currentGameScore.scorePoint(player);
            if (currentGameScore.isOver()) {
                applyGameResult();
            }
        }
    }

    public boolean isOver() {
        if (isTiebreak()) {
            return tiebreakGame.isOver();
        }
        return (gamesOne >= GAMES_TO_WIN || gamesTwo >= GAMES_TO_WIN)
                && Math.abs(gamesOne - gamesTwo) >= MIN_DIFFERENCE;
    }

    public int winner() {
        if (!isOver()) {
            throw new IllegalStateException("Set is not over yet");
        }
        return gamesOne > gamesTwo ? 1 : 2;
    }

    public boolean isTiebreak() {
        return tiebreakGame != null;
    }

    public GameScore getCurrentGameScore() {
        return isTiebreak() ? null : currentGameScore;
    }

    private void applyGameResult() {
        if (currentGameScore.winner() == 1) {
            gamesOne++;
        } else {
            gamesTwo++;
        }

        if (shouldStartTiebreak()) {
            tiebreakGame = new TiebreakGame();
        } else {
            currentGameScore = new GameScore();
        }
    }

    private void applyTiebreakResult() {
        int winner = tiebreakGame.winner();
        gamesOne = TiebreakGame.getGamesForPlayer(1, winner);
        gamesTwo = TiebreakGame.getGamesForPlayer(2, winner);
    }

    private boolean shouldStartTiebreak() {
        return gamesOne == TIEBREAK_TRIGGER && gamesTwo == TIEBREAK_TRIGGER;
    }
}