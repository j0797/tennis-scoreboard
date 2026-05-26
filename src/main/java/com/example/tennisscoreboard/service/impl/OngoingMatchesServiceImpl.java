package com.example.tennisscoreboard.service.impl;
import com.example.tennisscoreboard.model.TennisMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OngoingMatchesServiceImpl {

    // TODO: Нет интерфейса для этого класса. (см. файл "service.md" в этом же пакете)

    // Лучше создавать экземпляр этого сервиса (и использовать через интерфейс), чем использовать его как утилитный класс. Так можно будет проще заменить реализацию.

    private static final Map<UUID, TennisMatch> ongoingMatches = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(OngoingMatchesServiceImpl.class);

    // TODO: Метод должен имена игроков в String или DTO с именами игроков. Использование JPA Entity (Player) в этом сервисе жёстко связывает его со слоем персистентности и нарушает архитектурные границы.
    public static UUID createMatch(String playerOneName, String playerTwoName) {
        UUID matchId = UUID.randomUUID();
        ongoingMatches.put(matchId, new TennisMatch(new com.example.tennisscoreboard.model.Player(null, playerOneName),
                new com.example.tennisscoreboard.model.Player(null, playerTwoName)));
        return matchId;
    }

    public static TennisMatch getOngoingMatch(UUID matchId) {
        return ongoingMatches.get(matchId);
    }

    public static void addPoint(UUID matchId, int playerNumber) {
        TennisMatch match = ongoingMatches.get(matchId);
        if (match == null) {
            log.warn("Ongoing match not found: {}", matchId);
            return;
        }
        log.debug("Add point for match {} player {}", matchId, playerNumber);

        // TODO: MatchScoreCalculationService лучше внедрять через конструктор, чтобы следовать принципу Dependency Injection (DI) и эта зависимость была более явной.
        // Запускать начисление очка лучше в методе compute объекта ConcurrentHashMap
        match.scorePoint(playerNumber);
        if (match.isOver()) {
            log.info("Match {} finished. Winner: {}", matchId, match.winner().name());

            // TODO: restore in commit 5 - save finished match to DB
            // FinishedMatchesPersistenceServiceImpl persistence = new FinishedMatchesPersistenceServiceImpl();
            // persistence.save(match);

            // TODO: Удаление матча из хранилища (как и сохранение его в БД) из метода addPoint является побочным эффектом и нарушает Принцип наименьшего удивления. (см. файл "Принцип наименьшего удивления (Principle of Least Astonishment, POLA).md" в этом же пакете)
            ongoingMatches.remove(matchId);
        }
    }
}