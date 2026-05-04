package com.tuyensinh.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
//        } catch (Exception ex) {
//            throw new IllegalStateException("Cannot initialize Hibernate SessionFactory", ex);
//        }
        }catch (Throwable ex) {
            // Dòng này cực kỳ quan trọng để thấy lỗi thật sự là gì (ví dụ: sai password, thiếu driver)
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        SESSION_FACTORY.close();
    }
}
