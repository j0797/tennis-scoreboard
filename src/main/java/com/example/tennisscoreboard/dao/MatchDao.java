package com.example.tennisscoreboard.dao;

import com.example.tennisscoreboard.entity.Match;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class MatchDao extends BaseDao<Match> {

    // TODO: Тела методов стоит обернуть в try-catch и отлавливать HibernateException или PersistenceException.
        // Слой DAO должен перехватывать специфичные для технологии исключения (например, `HibernateException`)
        // и оборачивать их в свои, более общие исключения слоя доступа к данным (например, `DataAccessException`).
        // Это скрывает детали реализации от верхних слоёв и делает их независимыми от деталей реализации DAO.

    // Ключевые слова в тексте HQL-запросов (`from`, `where`) написаны в нижнем регистре.
        // Хотя это и не влияет на работоспособность, написание ключевых слов SQL/HQL в верхнем регистре (`UPPERCASE`) является общепринятым стандартом.
        // Это значительно улучшает читаемость запросов, так как визуально отделяет синтаксические конструкции языка от имён сущностей и полей.

    // Текст HQL запросов удобнее читать, когда они логично разбиты на строки, даже если они короткие.
        // Для визуального разделения запросов на строки лучше использовать текстовые блоки.

    // Лучше вынести тексты HQL запросов в `private static final` константы и дать им понятные имена.

    // Название параметра "name" тоже лучше вынести в именованную константу.

    // Лучше иметь разные методы для выборки с фильтром по имени и без него, а также для подсчёта количества,
        // чем собирать эту логику в одном методе. Если правила фильтрации поменяются,
        // то нужно будет изменить/дописать только некоторые методы, оставив логику выборки без фильтра без изменений.

    // Для выполнения регистронезависимого поиска по имени можно использовать возможности базы данных — в H2 есть ключевое слово `ILIKE`.

    // TODO: Проблема N+1 запросов в методе выборки матчей.
        //  Метод `findAll` выполняет HQL-запросы вида `"FROM Match m ..."`.
        // Сущность `Match` имеет связи `@ManyToOne` с `Player`, поэтому при выполнении такого запроса
        // Hibernate сначала получит список матчей (1 запрос), а затем он будет выполнять по 2 дополнительных `SELECT` запроса
        // для каждого матча, чтобы получить связанных с ним игроков. Если на странице 5 матчей,
        // это приведёт к 11 запросам вместо одного.

    public MatchDao(SessionFactory sessionFactory) {
        super(sessionFactory);
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