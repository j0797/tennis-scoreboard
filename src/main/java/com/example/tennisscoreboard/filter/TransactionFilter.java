package com.example.tennisscoreboard.filter;

import com.example.tennisscoreboard.util.HibernateUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebFilter("/*")
public class TransactionFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(TransactionFilter.class);

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try {
            chain.doFilter(request, response);
            session.getTransaction().commit();
        } catch (Exception e) {
            rollbackSafely(session, e);
            throw e;
        }
    }

    private void rollbackSafely(Session session, Exception original) {
        if (session.getTransaction().isActive()) {
            try {
                session.getTransaction().rollback();
            } catch (Exception rollbackEx) {
                log.error("Rollback failed", rollbackEx);
                original.addSuppressed(rollbackEx);
            }
        }
    }
}