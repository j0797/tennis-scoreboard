package com.example.tennisscoreboard.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public abstract class BaseDao<E> {
    protected final SessionFactory sessionFactory;
    private static final Logger log = LoggerFactory.getLogger(BaseDao.class);

    public BaseDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(E model) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(model);
        log.info("{} has been saved", model.getClass().getSimpleName());
    }
}
