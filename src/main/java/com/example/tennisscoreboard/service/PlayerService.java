package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.dao.PlayerDao;
import com.example.tennisscoreboard.entity.Player;
import com.example.tennisscoreboard.util.HibernateUtil;
import jakarta.persistence.PersistenceException;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class PlayerService {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private final PlayerDao playerDao = new PlayerDao(sessionFactory);

    public Player findOrCreatePlayer(String name) {
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
}
