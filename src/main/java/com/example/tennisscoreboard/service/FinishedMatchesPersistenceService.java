package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.dao.MatchDao;
import com.example.tennisscoreboard.dto.MatchDto;
import com.example.tennisscoreboard.dto.PaginationResponseDto;
import com.example.tennisscoreboard.entity.Match;
import com.example.tennisscoreboard.mapper.MatchMapper;
import com.example.tennisscoreboard.mapper.OngoingMatchMapper;
import com.example.tennisscoreboard.model.OngoingMatch;
import com.example.tennisscoreboard.util.HibernateUtil;
import org.hibernate.SessionFactory;

import java.util.List;

public class FinishedMatchesPersistenceService {
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private final MatchDao matchDao = new MatchDao(sessionFactory);
    private final OngoingMatchMapper mapper = OngoingMatchMapper.INSTANCE;
    private static final int DEFAULT_PAGE_SIZE = 5;

    public void save(OngoingMatch match) {
        Match finishedMatch = mapper.toEntity(match);
        matchDao.save(finishedMatch);
    }

    public PaginationResponseDto<MatchDto> getFinishedMatches(String playerName, Long currentPage) {
        String formattedName = (playerName == null || playerName.isBlank()) ? null : playerName.trim();
        int pageSize = DEFAULT_PAGE_SIZE;
        if (currentPage == null || currentPage < 1) {
            currentPage = 1L;
        }
        int offset = (int) ((currentPage - 1) * pageSize);

        Long totalMatches = matchDao.countAll(formattedName);
        Long totalPages = (long) Math.ceil((double) totalMatches / pageSize);

        List<Match> matches = matchDao.findAll(formattedName, offset, pageSize);
        List<MatchDto> matchDtos = matches.stream()
                .map(MatchMapper.INSTANCE::toDto)
                .toList();
        return new PaginationResponseDto<>(matchDtos, totalPages);
    }
}
