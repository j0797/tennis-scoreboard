package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.dao.MatchDao;
import com.example.tennisscoreboard.entity.Match;
import com.example.tennisscoreboard.entity.Player;
import com.example.tennisscoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.UUID;

public class MatchService {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private final MatchDao matchDao = new MatchDao(sessionFactory);
    private final PlayerService playerService = new PlayerService();

    public UUID createOngoingMatch(String playerOneName, String playerTwoName) {
        Player p1 = playerService.findOrCreatePlayer(playerOneName);
        Player p2 = playerService.findOrCreatePlayer(playerTwoName);
        return OngoingMatchesService.createMatch(p1, p2);
    }

    public Match getMatch(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return matchDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));
    }
}