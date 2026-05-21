package com.example.tennisscoreboard.dao;

import com.example.tennisscoreboard.entity.Match;
import com.example.tennisscoreboard.exception.DatabaseException;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

import java.util.List;

public class MatchDao extends BaseDao<Match> {

    private static final String PARAM_NAME = "name";

    private static final String COUNT_ALL = """
            SELECT COUNT(m)
            FROM Match m
            """;

    private static final String COUNT_BY_PLAYER_NAME = """
            SELECT COUNT(m)
            FROM Match m
            WHERE m.firstPlayer.name ILIKE :name
               OR m.secondPlayer.name ILIKE :name
            """;

    private static final String FIND_ALL = """
            FROM Match m
            JOIN FETCH m.firstPlayer
            JOIN FETCH m.secondPlayer
            JOIN FETCH m.winner
            ORDER BY m.id DESC
            """;

    private static final String FIND_BY_PLAYER_NAME = """
            FROM Match m
            JOIN FETCH m.firstPlayer
            JOIN FETCH m.secondPlayer
            JOIN FETCH m.winner
            WHERE m.firstPlayer.name ILIKE :name
               OR m.secondPlayer.name ILIKE :name
            ORDER BY m.id DESC
            """;

    public MatchDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public long countAll() {
        try {
            return getSession()
                    .createQuery(COUNT_ALL, Long.class)
                    .uniqueResult();
        } catch (HibernateException e) {
            throw new DatabaseException("Failed to count all matches", e);
        }
    }

    public long countByPlayerName(String playerName) {
        try {
            return getSession()
                    .createQuery(COUNT_BY_PLAYER_NAME, Long.class)
                    .setParameter(PARAM_NAME, "%" + playerName + "%")
                    .uniqueResult();
        } catch (HibernateException e) {
            throw new DatabaseException("Failed to count matches by player name: " + playerName, e);
        }
    }

    public List<Match> findAll(int offset, int pageSize) {
        try {
            return getSession()
                    .createQuery(FIND_ALL, Match.class)
                    .setFirstResult(offset)
                    .setMaxResults(pageSize)
                    .list();
        } catch (HibernateException e) {
            throw new DatabaseException("Failed to fetch matches", e);
        }
    }

    public List<Match> findByPlayerName(String playerName, int offset, int pageSize) {
        try {
            return getSession()
                    .createQuery(FIND_BY_PLAYER_NAME, Match.class)
                    .setParameter(PARAM_NAME, "%" + playerName + "%")
                    .setFirstResult(offset)
                    .setMaxResults(pageSize)
                    .list();
        } catch (HibernateException e) {
            throw new DatabaseException("Failed to fetch matches by player name: " + playerName, e);
        }
    }
}