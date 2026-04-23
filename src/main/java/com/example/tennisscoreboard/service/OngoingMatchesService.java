package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.entity.Player;
import com.example.tennisscoreboard.model.OngoingMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OngoingMatchesService {
    private static final Map<UUID, OngoingMatch> ongoingMatches = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(OngoingMatchesService.class);

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

        MatchScoreCalculationService.addPoint(match, playerNumber);
        if (match.isMatchOver()) {
            match.setWinner(playerNumber == 1 ? match.getPlayerOne() : match.getPlayerTwo());
            log.info("Match {} finished. Winner: {} ({}:{}, {}:{})",
                    matchId, match.getWinner().getName(),
                    match.getScore().getPlayerOneSets(), match.getScore().getPlayerTwoSets(),
                    match.getScore().getPlayerOneGames(), match.getScore().getPlayerTwoGames());
            FinishedMatchesPersistenceService persistence = new FinishedMatchesPersistenceService();
            persistence.save(match);
            ongoingMatches.remove(matchId);
        }
    }
}