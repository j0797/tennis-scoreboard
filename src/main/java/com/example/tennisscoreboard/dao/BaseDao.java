package com.example.tennisscoreboard.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public abstract class BaseDao<E> {

    // лучше сделать SessionFactory private и protected геттер для неё. (см. файл "Хрупкий базовый класс (The Fragile Base Class).md" в этом же пакете) А также можно сделать метод 'protected Session getSession()', который будет сам вызывать sessionFactory.getCurrentSession() и возвращать текущую сессию.
    protected final SessionFactory sessionFactory;

    // статические поля (static) обычно идут перед полями экземпляра, поэтому это поле должно быть объявлено раньше (находиться выше), чем SessionFactory
    private static final Logger log = LoggerFactory.getLogger(BaseDao.class);

    // можно использовать @RequiredArgsConstructor над классом вместо самописного конструктора
    public BaseDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // можно возвращать сохранённую Entity, чтобы работа с сохранённым объектом в клиентском коде была более явной
    // параметр можно переименовать в E entity
    public void save(E model) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(model);
        log.info("{} has been saved", model.getClass().getSimpleName());
    }
}
