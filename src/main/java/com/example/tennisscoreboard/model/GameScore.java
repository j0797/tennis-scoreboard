package com.example.tennisscoreboard.model;

import lombok.Getter;

@Getter
public class GameScore {
    private Points firstPlayer;
    private Points secondPlayer;

    public GameScore() {
        this.firstPlayer = Points.LOVE;
        this.secondPlayer = Points.LOVE;
    }

    public void scorePoint(int player) {
        if (isOver()) {
            throw new IllegalStateException("Cannot score a point in a finished game");
        }
        if (player == 1) {
            firstPlayer = nextPoint(firstPlayer, secondPlayer);
            if (firstPlayer != Points.ADVANTAGE) {
                secondPlayer = resetAdvantage(secondPlayer);
            }
        } else {
            secondPlayer = nextPoint(secondPlayer, firstPlayer);
            if (secondPlayer != Points.ADVANTAGE) {
                firstPlayer = resetAdvantage(firstPlayer);
            }
        }
    }

    public boolean isOver() {
        return firstPlayer == Points.WON || secondPlayer == Points.WON;
    }

    public int winner() {
        if (!isOver()) {
            throw new IllegalStateException("Game is not over yet");
        }
        return firstPlayer == Points.WON ? 1 : 2;
    }

    private Points resetAdvantage(Points points) {
        return points == Points.ADVANTAGE ? Points.FORTY : points;
    }

    private Points nextPoint(Points scoring, Points opponent) {
        return switch (scoring) {
            case LOVE, FIFTEEN, THIRTY -> scoring.next();
            case FORTY -> {
                if (opponent == Points.ADVANTAGE) {
                    yield Points.FORTY;
                } else if (opponent == Points.FORTY) {
                    yield Points.ADVANTAGE;
                } else {
                    yield Points.WON;
                }
            }
            case ADVANTAGE -> Points.WON;
            case WON -> throw new IllegalStateException("Cannot advance past WON");
        };
    }
}