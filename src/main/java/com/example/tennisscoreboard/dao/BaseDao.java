package com.example.tennisscoreboard.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseDao<E> {

    private static final Logger log = LoggerFactory.getLogger(BaseDao.class);
    private final SessionFactory sessionFactory;

    public BaseDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public E save(E entity) {
        getSession().persist(entity);
        log.info("{} has been saved", entity.getClass().getSimpleName());
        return entity;
    }
}
