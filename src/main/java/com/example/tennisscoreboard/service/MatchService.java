package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.dao.PlayerDao;
import com.example.tennisscoreboard.entity.Player;
import com.example.tennisscoreboard.util.HibernateUtil;
import org.hibernate.SessionFactory;

import java.util.UUID;

public class MatchService {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private final PlayerDao playerDao = new PlayerDao(sessionFactory);

    public UUID createMatch(String playerOneName, String playerTwoName) {
        var session = sessionFactory.getCurrentSession();
            session.beginTransaction();

            Player p1 = playerDao.findByName(playerOneName).orElseGet(() -> {
                Player p = new Player(playerOneName);
                playerDao.save(p);
                return p;
            });
            Player p2 = playerDao.findByName(playerTwoName).orElseGet(() -> {
                Player p = new Player(playerTwoName);
                playerDao.save(p);
                return p;
            });
            session.getTransaction().commit();
            return UUID.randomUUID();
        }
    }