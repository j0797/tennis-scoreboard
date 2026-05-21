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

    // TODO: Нет интерфейса для этого класса. (см. файл "service.md" в этом же пакете)

    // TODO: Этот сервис не должен ничего знать о деталях реализации DAO слоя, то есть не должен знать о SessionFactory.

    // TODO: MatchDao стоит внедрять через конструктор, а не создавать в этом классе.

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private final MatchDao matchDao = new MatchDao(sessionFactory);
    private final OngoingMatchMapper mapper = OngoingMatchMapper.INSTANCE;

    // Константы объявляются первыми (пишутся в самом верху) в классе.
    // Размер страницы по умолчанию более уместно хранить в сервлете, так как в идеале он должен приходить с фронтенда. А сервис должен принимать это значение в качестве аргумента в методы.
    private static final int DEFAULT_PAGE_SIZE = 5;

    public void save(OngoingMatch match) {
        Match finishedMatch = mapper.toEntity(match);
        matchDao.save(finishedMatch);
    }

    // Логику форматирования имени и рассчёта offset и totalPages можно вынести во вспомогательные private методы. Так код станет более читаемым.
    public PaginationResponseDto<MatchDto> getFinishedMatches(String playerName, Long currentPage) {
        String formattedName = (playerName == null || playerName.isBlank()) ? null : playerName.trim();
        int pageSize = DEFAULT_PAGE_SIZE;
        if (currentPage == null || currentPage < 1) {
            currentPage = 1L;
        }
        int offset = (int) ((currentPage - 1) * pageSize);

        Long totalMatches;
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