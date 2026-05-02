package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.entity.Player;
import com.example.tennisscoreboard.model.OngoingMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OngoingMatchesService {

    // TODO: Нет интерфейса для этого класса. (см. файл "service.md" в этом же пакете)

    // Лучше создавать экземпляр этого сервиса (и использовать через интерфейс), чем использовать его как утилитный класс. Так можно будет проще заменить реализацию.

    private static final Map<UUID, OngoingMatch> ongoingMatches = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(OngoingMatchesService.class);

    // TODO: Метод должен имена игроков в String или DTO с именами игроков. Использование JPA Entity (Player) в этом сервисе жёстко связывает его со слоем персистентности и нарушает архитектурные границы.
    public static UUID createMatch(Player playerOne, Player playerTwo) {
        UUID matchId = UUID.randomUUID();
        ongoingMatches.put(matchId, new OngoingMatch(playerOne, playerTwo));
        log.info("New match created with id {}: {} vs {}", matchId, playerOne.getName(), playerTwo.getName());
        return matchId;
    }

    public static OngoingMatch getOngoingMatch(UUID matchId) {
        log.debug("Fetching ongoing match with id {}", matchId);
        return ongoingMatches.get(matchId);
    }

    public static void addPoint(UUID matchId, int playerNumber) {
        OngoingMatch match = ongoingMatches.get(matchId);
        if (match == null) {
            log.warn("Ongoing match not found: {}", matchId);
            return;
        }
        log.debug("Add point for match {} player {}", matchId, playerNumber);

        // TODO: MatchScoreCalculationService лучше внедрять через конструктор, чтобы следовать принципу Dependency Injection (DI) и эта зависимость была более явной.
        // Запускать начисление очка лучше в методе compute объекта ConcurrentHashMap
        MatchScoreCalculationService.addPoint(match, playerNumber);

        if (match.isMatchOver()) {

            // TODO: Логика определения победителя должна находиться в самой доменной модели матча
            match.setWinner(playerNumber == 1 ? match.getPlayerOne() : match.getPlayerTwo());
            log.info("Match {} finished. Winner: {} ({}:{}, {}:{})",
                    matchId, match.getWinner().getName(),
                    match.getScore().getPlayerOneSets(), match.getScore().getPlayerTwoSets(),
                    match.getScore().getPlayerOneGames(), match.getScore().getPlayerTwoGames());

            // TODO: Создавать объект FinishedMatchesPersistenceService при каждом вызове этого метода (при каждом выигранном очке) крайне избыточно.
            FinishedMatchesPersistenceService persistence = new FinishedMatchesPersistenceService();

            // TODO: Обязанность запускать сохранение завершённых матчей нарушает SRP этого класса. Эта ответственность должна лежать на внешнем коде, например, сервисе-оркестраторе.
            persistence.save(match);

            // TODO: Удаление матча из хранилища (как и сохранение его в БД) из метода addPoint является побочным эффектом и нарушает Принцип наименьшего удивления. (см. файл "Принцип наименьшего удивления (Principle of Least Astonishment, POLA).md" в этом же пакете)
            ongoingMatches.remove(matchId);
        }
    }
}