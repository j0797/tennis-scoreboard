package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.model.MatchScore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OngoingMatchesService {
    private final Map<Long, MatchScore> scores = new ConcurrentHashMap<>();

    public MatchScore getScore(Long matchId) {
        return scores.computeIfAbsent(matchId, id -> new MatchScore());
    }

    public void addPoint(Long matchId, int playerNumber) {
        getScore(matchId).addPointToPlayer(playerNumber);
    }
}