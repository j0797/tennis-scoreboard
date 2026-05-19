package com.example.tennisscoreboard.model;

public class TiebreakGame {
    private static final int POINTS_TO_WIN = 7;
    private static final int MIN_DIFFERENCE = 2;
    private int pointsOne;
    private int pointsTwo;

    public void scorePoint(int player) {
        if (isOver()) {
            throw new IllegalStateException("Cannot score a point in a finished tiebreak");
        }
        if (player == 1) pointsOne++;
        else pointsTwo++;
    }

    public boolean isOver() {
        return (pointsOne >= POINTS_TO_WIN || pointsTwo >= POINTS_TO_WIN)
                && Math.abs(pointsOne - pointsTwo) >= MIN_DIFFERENCE;
    }

    public int winner() {
        if (!isOver()) {
            throw new IllegalStateException("Tiebreak is not over yet");
        }
        return pointsOne > pointsTwo ? 1 : 2;
    }

    public int getPointsOne() {
        return pointsOne;
    }

    public int getPointsTwo() {
        return pointsTwo;
    }

    public static int getGamesForPlayer(int player, int winner) {
        return player == winner ? 7 : 6;
    }
}