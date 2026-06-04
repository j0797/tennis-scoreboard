package com.example.tennisscoreboard.service.impl;

import com.example.tennisscoreboard.dto.ScoreDto;
import com.example.tennisscoreboard.exception.NotFoundException;
import com.example.tennisscoreboard.mapper.MatchScoreDisplayMapper;
import com.example.tennisscoreboard.model.TennisMatch;
import com.example.tennisscoreboard.service.MatchService;
import com.example.tennisscoreboard.service.OngoingMatchesService;

import java.util.UUID;

public class MatchServiceImpl implements MatchService {

    private final OngoingMatchesService ongoingMatchesService;

    public MatchServiceImpl(OngoingMatchesService ongoingMatchesService) {
        this.ongoingMatchesService = ongoingMatchesService;
    }

    @Override
    public UUID createMatch(String firstPlayerName, String secondPlayerName) {
        return ongoingMatchesService.createMatch(firstPlayerName, secondPlayerName);
    }

    @Override
    public ScoreDto getMatchScore(UUID matchId) {
        TennisMatch match = ongoingMatchesService.getOngoingMatch(matchId);
        if (match == null) {
            throw new NotFoundException("Match not found");
        }
        return MatchScoreDisplayMapper.toDisplayDto(match);
    }

    @Override
    public ScoreDto addPoint(UUID matchId, int playerNumber) {
        TennisMatch match = ongoingMatchesService.getOngoingMatch(matchId);
        if (match == null) {
            throw new NotFoundException("Match not found");
        }
        ongoingMatchesService.addPoint(matchId, playerNumber);
        if (match.isOver()) {
            return MatchScoreDisplayMapper.toDisplayDto(match);
        }
        return null;
    }
}