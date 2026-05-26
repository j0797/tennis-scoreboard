package com.example.tennisscoreboard.service.impl;

import com.example.tennisscoreboard.dao.PlayerDao;
import com.example.tennisscoreboard.entity.Player;
import com.example.tennisscoreboard.exception.DatabaseException;
import com.example.tennisscoreboard.util.HibernateUtil;
import jakarta.persistence.PersistenceException;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PlayerServiceImpl {

    // TODO: Нет интерфейса для этого класса. (см. файл "service.md" в этом же пакете)

    // TODO: Этот сервис не должен ничего знать о деталях реализации DAO слоя, то есть не должен знать о SessionFactory.

    // TODO: PlayerDao стоит внедрять через конструктор, а не создавать в этом классе.

    private static final Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private final PlayerDao playerDao = new PlayerDao(sessionFactory);


    public Player findOrCreatePlayer(String name) {
        Optional<Player> existing = playerDao.findByName(name);
        if (existing.isPresent()) {
            log.debug("Player found: {}", name);
            return existing.get();
        }
        try {
            Player newPlayer = new Player(name);
            playerDao.save(newPlayer);
            sessionFactory.getCurrentSession().flush(); // нет необходимости вручную вызывать здесь .flush()
            log.info("New player created: {} with id {}", name, newPlayer.getId());
            return newPlayer;
        } catch (PersistenceException e) {
            log.warn("Duplicate or concurrent insert for player: {}. Retrying find.", name);
            sessionFactory.getCurrentSession().clear(); // нет необходимости вручную вызывать здесь .clear()

            // Снова искать игрока имеет смысл только если произошло исключение из-за нарушения уникальности. Перед поиском здесь стоит это проверить. Иначе нужно обработать исключение.
            return playerDao.findByName(name)
                    .orElseThrow(() -> new DatabaseException("Player not found after concurrent insert: " + name, e));
        }
    }
}