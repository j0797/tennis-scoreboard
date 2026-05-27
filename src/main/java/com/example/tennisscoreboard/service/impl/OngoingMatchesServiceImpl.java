package com.example.tennisscoreboard.service.impl;

import com.example.tennisscoreboard.model.TennisMatch;
import com.example.tennisscoreboard.service.FinishedMatchesPersistenceService;
import com.example.tennisscoreboard.service.OngoingMatchesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OngoingMatchesServiceImpl implements OngoingMatchesService {

    private static final Logger log = LoggerFactory.getLogger(OngoingMatchesServiceImpl.class);
    private static final Map<UUID, TennisMatch> ongoingMatches = new ConcurrentHashMap<>();

    private final FinishedMatchesPersistenceService persistenceService;

    public OngoingMatchesServiceImpl(FinishedMatchesPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @Override
    public UUID createMatch(String playerOneName, String playerTwoName) {
        UUID matchId = UUID.randomUUID();
        ongoingMatches.put(matchId, new TennisMatch(new com.example.tennisscoreboard.model.Player(null, playerOneName),
                new com.example.tennisscoreboard.model.Player(null, playerTwoName)));
        log.info("New match created: {} vs {}, id={}", playerOneName, playerTwoName, matchId);
        return matchId;
    }

    @Override
    public TennisMatch getOngoingMatch(UUID matchId) {
        return ongoingMatches.get(matchId);
    }

    @Override
    public void addPoint(UUID matchId, int playerNumber) {
        TennisMatch match = ongoingMatches.get(matchId);
        if (match == null) {
            log.warn("Ongoing match not found: {}", matchId);
            return;
        }
        if (match.isOver()) {
            log.warn("Match {} is already over, ignoring point", matchId);
            return;
        }
        match.scorePoint(playerNumber);
        if (match.isOver()) {
            log.info("Match {} finished. Winner: {}", matchId, match.winner().name());
            persistenceService.save(match);
            ongoingMatches.remove(matchId);
        }
    }
}