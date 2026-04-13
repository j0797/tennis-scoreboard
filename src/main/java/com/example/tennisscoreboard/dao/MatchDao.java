package com.example.tennisscoreboard.dao;

import com.example.tennisscoreboard.entity.Match;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
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

    public Long countAll(String playerName) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "select count(m) from Match m where " +
                "(m.player1.name like :name or m.player2.name like :name)";
        if (playerName == null || playerName.isBlank()) {
            hql = "select count(m) from Match m";
            return session.createQuery(hql, Long.class).uniqueResult();
        }
        return session.createQuery(hql, Long.class)
                .setParameter("name", "%" + playerName + "%")
                .uniqueResult();
    }

    public List<Match> findAll(String playerName, int offset, int pageSize) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "from Match m where " +
                "(m.player1.name like :name or m.player2.name like :name) " +
                "order by m.id desc";
        if (playerName == null || playerName.isBlank()) {
            hql = "from Match m order by m.id desc";
            return session.createQuery(hql, Match.class)
                    .setFirstResult(offset)
                    .setMaxResults(pageSize)
                    .list();
        }
        return session.createQuery(hql, Match.class)
                .setParameter("name", "%" + playerName + "%")
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .list();
    }
}