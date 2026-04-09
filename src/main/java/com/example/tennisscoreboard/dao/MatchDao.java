package com.example.tennisscoreboard.dao;

import com.example.tennisscoreboard.entity.Match;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class MatchDao extends BaseDao<Long, Match> {
    public MatchDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<Match> findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                        "SELECT m FROM Match m " +
                                "JOIN FETCH m.player1 " +
                                "JOIN FETCH m.player2 " +
                                "WHERE m.id = :id", Match.class)
                .setParameter("id", id)
                .uniqueResultOptional();
    }

    public void update(Match match) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(match);
    }
}