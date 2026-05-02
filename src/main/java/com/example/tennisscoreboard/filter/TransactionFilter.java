package com.example.tennisscoreboard.filter;

import com.example.tennisscoreboard.exception.DatabaseException;
import com.example.tennisscoreboard.exception.NotFoundException;
import com.example.tennisscoreboard.exception.ValidationException;
import com.example.tennisscoreboard.util.HibernateUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebFilter("/*")
public class TransactionFilter implements Filter {

    // Класс может наследоваться от HttpFilter и переопределять его метод
        // protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain),
        // тогда не придётся ServletResponse к HttpServletResponse вручную.

    // TODO: Класс называется TransactionFilter, однако занимается не только транзакциями, но и обработкой исключений.
        // Это нарушает принцип единой ответственности (SRP). Стоит разделить эти обязанности на разные классы.

    // Все повторяющиеся или важные строковые литералы лучше вынести в `private static final` константы с понятными именами.
        // Именованная константа делает код более семантически понятным.

    // TODO: В блоке `catch` вызов `transaction.rollback()` не обёрнут в `try-catch`.
        // Если во время отката транзакции произойдёт ещё одно исключение (например, из-за проблем с сетевым соединением с БД),
        // это новое исключение "замаскирует" исходную ошибку, которая инициировала откат.
        // В логах останется только ошибка отката, и разработчик не сможет узнать, что послужило первопричиной сбоя, что сильно усложняет отладку.
        //
        // Стоит обернуть `transaction.rollback()` в собственный блок `try-catch` и, в случае ошибки,
        // добавить новое исключение к исходному с помощью `originalException.addSuppressed(rollbackException)`.

    private static final Logger log = LoggerFactory.getLogger(TransactionFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException {
        HttpServletResponse resp = (HttpServletResponse) response;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            chain.doFilter(request, response);
            session.getTransaction().commit();
        } catch (ValidationException | NotFoundException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            int status = e instanceof ValidationException ? HttpServletResponse.SC_BAD_REQUEST : HttpServletResponse.SC_NOT_FOUND;
            log.warn("Client error: {}", e.getMessage());
            resp.setStatus(status);
            resp.setContentType("text/plain;charset=UTF-8");

            // TODO: Фильтр отправляет сообщение из исключения (`e.getMessage()`) напрямую пользователю
                // Хотя в этом приложении для ValidationException и NotFoundException данная практика не приводит к проблемам
                // из-за того, что сообщения "безопасные", она все равно является плохим архитектурным решением.
            resp.getWriter().write(e.getMessage());
        } catch (DatabaseException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Database error", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("text/plain;charset=UTF-8");
            resp.getWriter().write("Database error. Please try again later.");
        } catch (Exception e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Unexpected error", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("text/plain;charset=UTF-8");
            resp.getWriter().write("An internal error occurred. Please try again later.");
        }
    }
}
