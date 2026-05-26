package com.example.tennisscoreboard.mapper;

import com.example.tennisscoreboard.dto.ScoreDto;
import com.example.tennisscoreboard.model.MatchScore;
import com.example.tennisscoreboard.model.OngoingMatch;

public class MatchScoreDisplayMapper {

    public static ScoreDto toDisplayDto(OngoingMatch match) {
        MatchScore score = match.getScore();

        String points1 = formatPoints(
                score.getPlayerOnePoints(),
                score.isPlayerOneAdvantage(),
                score.isPlayerTwoAdvantage()
        );
        String points2 = formatPoints(
                score.getPlayerTwoPoints(),
                score.isPlayerTwoAdvantage(),
                score.isPlayerOneAdvantage()
        );

        String games = score.getPlayerOneGames() + ":" + score.getPlayerTwoGames();
        String sets = score.getPlayerOneSets() + ":" + score.getPlayerTwoSets();

        String tieBreakPoints = match.isTieBreak()
                ? (score.getPlayerOneTieBreakPoints() + ":" + score.getPlayerTwoTieBreakPoints())
                : null;

        return new ScoreDto(points1, points2, games, sets, match.isTieBreak(), tieBreakPoints);
    }

    // Метод раскодирует количество очков в реальный счёт в гейме. Обязанность хранить счёт в корректных величинах лежит на доменной модели.
    // Это исправится автоматически после проведения декомпозиции и рефакторинга доменных моделей.
    private static String formatPoints(int points, boolean advantage, boolean opponentAdvantage) {
        if (advantage) {
            return "AD";
        }

        if (opponentAdvantage) {
            return "40";
        }

        return switch (points) {
            case 0 -> "0";
            case 1 -> "15";
            case 2 -> "30";
            case 3 -> "40";
            default ->
                    "40"; // По умолчанию не должно возвращаться значение 40. В текущей реализации здесь должно выбрасываться исплючение.
        };
    }
}
