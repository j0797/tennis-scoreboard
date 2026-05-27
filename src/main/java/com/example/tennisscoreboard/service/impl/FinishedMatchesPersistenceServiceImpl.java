package com.example.tennisscoreboard.service.impl;

import com.example.tennisscoreboard.dao.MatchDao;
import com.example.tennisscoreboard.dto.MatchDto;
import com.example.tennisscoreboard.dto.PaginationResponseDto;
import com.example.tennisscoreboard.entity.Match;
import com.example.tennisscoreboard.entity.Player;
import com.example.tennisscoreboard.mapper.MatchMapper;
import com.example.tennisscoreboard.model.TennisMatch;
import com.example.tennisscoreboard.service.FinishedMatchesPersistenceService;
import com.example.tennisscoreboard.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FinishedMatchesPersistenceServiceImpl implements FinishedMatchesPersistenceService {

    private static final Logger log = LoggerFactory.getLogger(FinishedMatchesPersistenceServiceImpl.class);
    private final MatchDao matchDao;
    private final PlayerService playerService;

    public FinishedMatchesPersistenceServiceImpl(MatchDao matchDao, PlayerService playerService) {
        this.matchDao = matchDao;
        this.playerService = playerService;
    }

    @Override
    public void save(TennisMatch tennisMatch) {
        Player firstPlayer = playerService.findOrCreatePlayer(tennisMatch.getPlayerOne().name());
        Player secondPlayer = playerService.findOrCreatePlayer(tennisMatch.getPlayerTwo().name());
        Player winner = playerService.findOrCreatePlayer(tennisMatch.winner().name());

        Match match = new Match(firstPlayer, secondPlayer, winner);
        matchDao.save(match);
        log.info("Match saved: {} vs {}, winner: {}",
                firstPlayer.getName(), secondPlayer.getName(), winner.getName());
    }

    // Логику форматирования имени и рассчёта offset и totalPages можно вынести во вспомогательные private методы. Так код станет более читаемым.
    @Override
    public PaginationResponseDto<MatchDto> getFinishedMatches(String playerName, Long currentPage, int pageSize) {
        String formattedName = (playerName == null || playerName.isBlank()) ? null : playerName.trim();
        if (currentPage == null || currentPage < 1) {
            currentPage = 1L;
        }
        int offset = (int) ((currentPage - 1) * pageSize);

        long totalMatches;
        List<Match> matches;

        if (formattedName == null || formattedName.isBlank()) {
            totalMatches = matchDao.countAll();
            matches = matchDao.findAll(offset, pageSize);
        } else {
            totalMatches = matchDao.countByPlayerName(formattedName);
            matches = matchDao.findByPlayerName(formattedName, offset, pageSize);
        }
        Long totalPages = (long) Math.ceil((double) totalMatches / pageSize);

        // Метод, принимающий List<Match> и возвращающий List<MatchDto> можно добавить в MatchMapper и перенести эту логику в него.
        List<MatchDto> matchDtos = matches.stream()
                .map(MatchMapper.INSTANCE::toDto)
                .toList();
        return new PaginationResponseDto<>(matchDtos, totalPages);
    }
}