package com.example.tennisscoreboard.service.impl;

import com.example.tennisscoreboard.dao.PlayerDao;
import com.example.tennisscoreboard.entity.Player;
import com.example.tennisscoreboard.exception.DatabaseException;
import com.example.tennisscoreboard.service.PlayerService;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PlayerServiceImpl implements PlayerService {

    private static final Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);
    private final PlayerDao playerDao;

    public PlayerServiceImpl(PlayerDao playerDao) {
        this.playerDao = playerDao;
    }

    @Override
    public Player findOrCreatePlayer(String name) {
        Optional<Player> existing = playerDao.findByName(name);
        if (existing.isPresent()) {
            log.debug("Player found: {}", name);
            return existing.get();
        }
        try {
            Player newPlayer = new Player(name);
            playerDao.save(newPlayer);
            log.info("New player created: {} with id {}", name, newPlayer.getId());
            return newPlayer;
        } catch (PersistenceException e) {
            log.warn("Duplicate or concurrent insert for player: {}. Retrying find.", name);

            // Снова искать игрока имеет смысл только если произошло исключение из-за нарушения уникальности. Перед поиском здесь стоит это проверить. Иначе нужно обработать исключение.
            return playerDao.findByName(name)
                    .orElseThrow(() -> new DatabaseException("Player not found after concurrent insert: " + name, e));
        }
    }
}