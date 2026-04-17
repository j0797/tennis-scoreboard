package com.example.tennisscoreboard.dao;

import com.example.tennisscoreboard.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class PlayerDao extends BaseDao<Player> {
    public PlayerDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<Player> findByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Player where name = :name", Player.class)
                .setParameter("name", name)
                .uniqueResultOptional();
    }
}
