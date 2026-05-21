package com.example.tennisscoreboard.dao;

import com.example.tennisscoreboard.entity.Player;
import com.example.tennisscoreboard.exception.DatabaseException;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class PlayerDao extends BaseDao<Player> {

    private static final String FIND_BY_NAME = """
            FROM Player
            WHERE name = :name
            """;

    private static final String PARAM_NAME = "name";

    public PlayerDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<Player> findByName(String name) {
        try {
            return getSession()
                    .createQuery(FIND_BY_NAME, Player.class)
                    .setParameter(PARAM_NAME, name)
                    .uniqueResultOptional();
        } catch (HibernateException e) {
            throw new DatabaseException("Failed to find player by name: " + name, e);

        }
    }
}