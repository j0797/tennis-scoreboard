package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.dto.MatchDto;
import com.example.tennisscoreboard.dto.PaginationResponseDto;
import com.example.tennisscoreboard.model.TennisMatch;

public interface FinishedMatchesPersistenceService {
    void save(TennisMatch tennisMatch);

    PaginationResponseDto<MatchDto> getFinishedMatches(String playerName, Long currentPage, int pageSize);
}