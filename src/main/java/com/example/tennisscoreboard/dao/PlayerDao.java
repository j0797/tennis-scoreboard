package com.example.tennisscoreboard.dao;

import com.example.tennisscoreboard.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class PlayerDao extends BaseDao<Player> {

    // TODO: Тело метода стоит обернуть в try-catch и отлавливать HibernateException или PersistenceException.
        //  Слой DAO должен перехватывать специфичные для технологии исключения (например, `HibernateException`)
        //  и оборачивать их в свои, более общие исключения слоя доступа к данным (например, `DataAccessException`).
        //  Это скрывает детали реализации от верхних слоёв и делает их независимыми от деталей реализации DAO.

    // Ключевые слова в тексте HQL-запроса (`from`, `where`) написаны в нижнем регистре.
        // Хотя это и не влияет на работоспособность, написание ключевых слов SQL/HQL в верхнем регистре (`UPPERCASE`) является общепринятым стандартом.
        // Это значительно улучшает читаемость запросов, так как визуально отделяет синтаксические конструкции языка от имён сущностей и полей.

    // Текст HQL запроса удобнее читать, когда он логично разбит на строки, даже если он короткий.
        // Для визуального разделения запросов на строки лучше использовать текстовые блоки

    // Лучше вынести текст HQL запроса в `private static final` константу и дать ей понятное имя.

    // Название параметра "name" тоже лучше вынести в именованную константу

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
