package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.dao.MatchDao;
import com.example.tennisscoreboard.dao.PlayerDao;
import com.example.tennisscoreboard.entity.Match;
import com.example.tennisscoreboard.entity.Player;
import com.example.tennisscoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class MatchService {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private final PlayerDao playerDao = new PlayerDao(sessionFactory);
    private final MatchDao matchDao = new MatchDao(sessionFactory);

    public Long createMatch(String playerOneName, String playerTwoName) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        try {
            Player p1 = playerDao.findByName(playerOneName)
                    .orElseGet(() -> playerDao.save(new Player(playerOneName)));
            Player p2 = playerDao.findByName(playerTwoName)
                    .orElseGet(() -> playerDao.save(new Player(playerTwoName)));

            Match match = Match.builder()
                    .player1(p1)
                    .player2(p2)
                    .winner(null)
                    .build();

            matchDao.save(match);
            session.getTransaction().commit();
            return match.getId();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Failed to create match", e);
        }
    }

    public Match getMatch(Long id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        try {
            Match match = matchDao.findById(id)
                    .orElseThrow(() -> new RuntimeException("Match not found"));
            session.getTransaction().commit();
            return match;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
    }
}