package com.example.tennisscoreboard.model;

import lombok.Getter;

@Getter
public class TennisMatch {
    private static final int SETS_TO_WIN = 2;
    private final Player playerOne;
    private final Player playerTwo;
    private int setsOne;
    private int setsTwo;
    private SetScore currentSet;

    public TennisMatch(Player playerOne, Player playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.currentSet = new SetScore();
    }

    public void scorePoint(int player) {
        if (isOver()) {
            throw new IllegalStateException("Cannot score a point in a finished match");
        }
        currentSet.scorePoint(player);
        if (currentSet.isOver()) {
            applySetResult();
        }
    }

    public boolean isOver() {
        return setsOne >= SETS_TO_WIN || setsTwo >= SETS_TO_WIN;
    }

    public Player winner() {
        if (!isOver()) {
            throw new IllegalStateException("Match is not over yet");
        }
        return setsOne > setsTwo ? playerOne : playerTwo;
    }

    private void applySetResult() {
        if (currentSet.winner() == 1) setsOne++;
        else setsTwo++;
        if (!isOver()) {
            currentSet = new SetScore();
        }
    }
}
