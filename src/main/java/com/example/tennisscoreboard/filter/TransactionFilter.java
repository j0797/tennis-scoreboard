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
