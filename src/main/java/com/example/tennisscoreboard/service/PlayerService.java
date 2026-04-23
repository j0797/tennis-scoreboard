package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.dao.PlayerDao;
import com.example.tennisscoreboard.entity.Player;
import com.example.tennisscoreboard.exception.DatabaseException;
import com.example.tennisscoreboard.util.HibernateUtil;
import jakarta.persistence.PersistenceException;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PlayerService {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private final PlayerDao playerDao = new PlayerDao(sessionFactory);
    private static final Logger log = LoggerFactory.getLogger(PlayerService.class);

    public Player findOrCreatePlayer(String name) {
        Optional<Player> existing = playerDao.findByName(name);
        if (existing.isPresent()) {
            log.debug("Player found: {}", name);
            return existing.get();
        }
        try {
            Player newPlayer = new Player(name);
            playerDao.save(newPlayer);
            sessionFactory.getCurrentSession().flush();
            log.info("New player created: {} with id {}", name, newPlayer.getId());
            return newPlayer;
        } catch (PersistenceException e) {
            log.warn("Duplicate or concurrent insert for player: {}. Retrying find.", name);
            sessionFactory.getCurrentSession().clear();
            return playerDao.findByName(name)
                    .orElseThrow(() -> new DatabaseException("Player not found after concurrent insert: " + name, e));
        }
    }
}
