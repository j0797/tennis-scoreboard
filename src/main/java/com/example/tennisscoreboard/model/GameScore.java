package com.example.tennisscoreboard.model;

import lombok.Getter;

@Getter
public class GameScore {
    private Points playerOne;
    private Points playerTwo;

    public GameScore() {
        this.playerOne = Points.LOVE;
        this.playerTwo = Points.LOVE;
    }

    public void scorePoint(int player) {
        if (isOver()) {
            throw new IllegalStateException("Cannot score a point in a finished game");
        }
        if (player == 1) {
            playerOne = nextPoint(playerOne, playerTwo);
            if (playerOne != Points.ADVANTAGE) {
                playerTwo = resetAdvantage(playerTwo);
            }
        } else {
            playerTwo = nextPoint(playerTwo, playerOne);
            if (playerTwo != Points.ADVANTAGE) {
                playerOne = resetAdvantage(playerOne);
            }
        }
    }

    public boolean isOver() {
        return playerOne == Points.WON || playerTwo == Points.WON;
    }

    public int winner() {
        if (!isOver()) throw new IllegalStateException("Game is not over yet");
        return playerOne == Points.WON ? 1 : 2;
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