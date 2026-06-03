package com.example.tennisscoreboard.config;

import com.example.tennisscoreboard.dao.MatchDao;
import com.example.tennisscoreboard.dao.PlayerDao;
import com.example.tennisscoreboard.service.FinishedMatchesPersistenceService;
import com.example.tennisscoreboard.service.MatchService;
import com.example.tennisscoreboard.service.OngoingMatchesService;
import com.example.tennisscoreboard.service.PlayerService;
import com.example.tennisscoreboard.service.impl.FinishedMatchesPersistenceServiceImpl;
import com.example.tennisscoreboard.service.impl.MatchServiceImpl;
import com.example.tennisscoreboard.service.impl.OngoingMatchesServiceImpl;
import com.example.tennisscoreboard.service.impl.PlayerServiceImpl;
import com.example.tennisscoreboard.util.HibernateUtil;
import lombok.Getter;
import org.hibernate.SessionFactory;

@Getter
public class ApplicationContext {

    private final MatchService matchService;
    private final FinishedMatchesPersistenceService persistenceService;

    public ApplicationContext() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        PlayerDao playerDao = new PlayerDao(sessionFactory);
        MatchDao matchDao = new MatchDao(sessionFactory);
        PlayerService playerService = new PlayerServiceImpl(playerDao);
        FinishedMatchesPersistenceService persistenceService =
                new FinishedMatchesPersistenceServiceImpl(matchDao, playerService);
        OngoingMatchesService ongoingMatchesService = new OngoingMatchesServiceImpl(persistenceService);
        this.matchService = new MatchServiceImpl(ongoingMatchesService);
        this.persistenceService = persistenceService;
    }
}
