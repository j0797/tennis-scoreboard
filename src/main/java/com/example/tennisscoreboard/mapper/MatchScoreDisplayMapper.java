package com.example.tennisscoreboard.mapper;

import com.example.tennisscoreboard.dto.ScoreDto;
import com.example.tennisscoreboard.model.*;

public class MatchScoreDisplayMapper {

    public static ScoreDto toDisplayDto(TennisMatch match) {
        SetScore currentSet = match.getCurrentSet();

        String points1, points2;
        if (match.isOver()) {
            points1 = "0";
            points2 = "0";
        } else if (currentSet.isTiebreak()) {
            TiebreakGame tb = currentSet.getTiebreakGame();
            points1 = String.valueOf(tb.getPointsOne());
            points2 = String.valueOf(tb.getPointsTwo());
        } else {
            GameScore currentGame = currentSet.getCurrentGameScore();
            points1 = formatPoints(currentGame.getPlayerOne());
            points2 = formatPoints(currentGame.getPlayerTwo());
        }

        String games = currentSet.getGamesOne() + ":" + currentSet.getGamesTwo();
        String sets = match.getSetsOne() + ":" + match.getSetsTwo();
        String winnerName = match.isOver() ? match.winner().name() : null;
        String tieBreakPoints = (!match.isOver() && currentSet.isTiebreak())
                ? points1 + ":" + points2
                : null;

        return new ScoreDto(
                match.getPlayerOne().name(),
                match.getPlayerTwo().name(),
                winnerName,
                points1, points2,
                games, sets,
                !match.isOver() && currentSet.isTiebreak(),
                tieBreakPoints
        );
    }

    private static String formatPoints(Points points) {
        return switch (points) {
            case LOVE -> "0";
            case FIFTEEN -> "15";
            case THIRTY -> "30";
            case FORTY -> "40";
            case ADVANTAGE -> "AD";
            case WON -> throw new IllegalStateException("WON is not a displayable point value");
        };
    }
}