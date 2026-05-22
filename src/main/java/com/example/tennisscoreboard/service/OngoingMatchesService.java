package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.model.TennisMatch;

import java.util.UUID;

public interface OngoingMatchesService {
    UUID createMatch(String playerOneName, String playerTwoName);

    TennisMatch getOngoingMatch(UUID matchId);

    void addPoint(UUID matchId, int player);
}