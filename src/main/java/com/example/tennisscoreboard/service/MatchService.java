package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.dto.ScoreDto;

import java.util.UUID;

public interface MatchService {
    UUID createMatch(String firstPlayerName, String secondPlayerName);

    ScoreDto getMatchScore(UUID matchId);

    ScoreDto addPoint(UUID matchId, int playerNumber);
}