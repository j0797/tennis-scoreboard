package com.example.tennisscoreboard.config;

import com.example.tennisscoreboard.util.HibernateUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class TennisContextListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(TennisContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            HibernateUtil.getSessionFactory();
            log.info("Hibernate SessionFactory initialized successfully");
        } catch (Throwable t) {
            log.error("Hibernate initialization failed", t);
            throw new RuntimeException("Failed to initialize Hibernate", t);
        }

        ApplicationContext appContext = new ApplicationContext();
        sce.getServletContext().setAttribute("appContext", appContext);
        log.info("ApplicationContext created and stored in ServletContext");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        HibernateUtil.shutdown();
        log.info("Hibernate SessionFactory closed");
    }
}