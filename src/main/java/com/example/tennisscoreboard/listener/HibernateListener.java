package com.example.tennisscoreboard.listener;

import com.example.tennisscoreboard.util.HibernateUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class HibernateListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(HibernateListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            HibernateUtil.getSessionFactory();
            log.info("Hibernate SessionFactory initialized successfully");
        } catch (Throwable t) {
            log.error("Hibernate initialization failed", t);
            throw new RuntimeException("Failed to initialize Hibernate", t);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        HibernateUtil.shutdown();
        log.info("Hibernate SessionFactory closed");
    }
}