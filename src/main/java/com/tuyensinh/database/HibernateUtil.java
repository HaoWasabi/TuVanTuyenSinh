package com.tuyensinh.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            return buildSessionFactory("hibernate.cfg.xml");
        } catch (Throwable ex) {
            System.err.println("Failed to load hibernate.cfg.xml: " + ex.getMessage());
            try {
                return buildSessionFactory("(no_password)hibernate.cfg.xml");
            } catch (Throwable fallbackEx) {
                System.err.println("Failed to load (no_password)hibernate.cfg.xml: " + fallbackEx.getMessage());
                throw new ExceptionInInitializerError(fallbackEx);
            }
        }
    }

    private static SessionFactory buildSessionFactory(String resourceName) {
        return new Configuration().configure(resourceName).buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        if (SESSION_FACTORY != null) {
            SESSION_FACTORY.close();
        }
    }
}
