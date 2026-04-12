package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.dao.MatchDao;
import com.example.tennisscoreboard.dao.PlayerDao;
import com.example.tennisscoreboard.entity.Match;
import com.example.tennisscoreboard.entity.Player;
import com.example.tennisscoreboard.util.HibernateUtil;
import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Optional;
import java.util.UUID;

public class MatchService {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private final PlayerDao playerDao = new PlayerDao(sessionFactory);
    private final MatchDao matchDao = new MatchDao(sessionFactory);

    public UUID createOngoingMatch(String playerOneName, String playerTwoName) {
        Player p1 = findOrCreatePlayer(playerOneName);
        Player p2 = findOrCreatePlayer(playerTwoName);
        return OngoingMatchesService.createMatch(p1, p2);
    }

    private Player findOrCreatePlayer(String name) {
        Optional<Player> existing = playerDao.findByName(name);
        if (existing.isPresent()) {
            return existing.get();
        }
        try {
            Player newPlayer = new Player(name);
            playerDao.save(newPlayer);
            sessionFactory.getCurrentSession().flush();
            return newPlayer;
        } catch (PersistenceException e) {
            sessionFactory.getCurrentSession().clear();
            return playerDao.findByName(name).orElseThrow();
        }
    }

    public Match getMatch(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return matchDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));
    }
}