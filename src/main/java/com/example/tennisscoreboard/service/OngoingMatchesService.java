package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.entity.Player;
import com.example.tennisscoreboard.model.OngoingMatch;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OngoingMatchesService {
    private static final Map<UUID, OngoingMatch> ongoingMatches = new ConcurrentHashMap<>();

    public static UUID createMatch(Player playerOne, Player playerTwo) {
        UUID matchId = UUID.randomUUID();
        ongoingMatches.put(matchId, new OngoingMatch(playerOne, playerTwo));
        return matchId;
    }

    public static OngoingMatch getMatch(UUID matchId) {
        return ongoingMatches.get(matchId);
    }

    public static void addPoint(UUID matchId, int playerNumber) {
        OngoingMatch match = ongoingMatches.get(matchId);
        if (match == null) return;

        MatchScoreCalculationService.addPoint(match, playerNumber);
        if (match.isMatchOver()) {
            match.setWinner(playerNumber == 1 ? match.getPlayerOne() : match.getPlayerTwo());
            FinishedMatchesPersistenceService persistence = new FinishedMatchesPersistenceService();
            persistence.save(match);
            ongoingMatches.remove(matchId);
        }
    }

    public static void deleteMatch(UUID matchId) {
        ongoingMatches.remove(matchId);
    }
}