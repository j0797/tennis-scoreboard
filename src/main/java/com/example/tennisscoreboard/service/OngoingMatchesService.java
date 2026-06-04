package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.model.TennisMatch;

import java.util.UUID;

public interface OngoingMatchesService {
    UUID createMatch(String firstPlayerName, String secondPlayerName);

    TennisMatch getOngoingMatch(UUID matchId);

    void addPoint(UUID matchId, int player);
}