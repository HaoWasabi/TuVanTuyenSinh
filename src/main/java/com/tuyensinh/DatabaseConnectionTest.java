package com.tuyensinh;

import com.tuyensinh.database.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class DatabaseConnectionTest {

    public static void main(String[] args) {
        System.out.println("========== DATABASE CONNECTION TEST START ==========");

        try {
            // 1. Lấy SessionFactory từ HibernateUtil
            System.out.println("[...] Đang khởi tạo Hibernate SessionFactory...");
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

            // 2. Thử mở một Session
            System.out.println("[...] Đang thử mở kết nối (Session) tới Database...");
            try (Session session = sessionFactory.openSession()) {
                session.doWork(connection -> {
                    if (connection.isValid(2)) { // Timeout 2 giây
                        System.out.println("[✓] KẾT NỐI THÀNH CÔNG!");
                        System.out.println("    - Database: " + connection.getMetaData().getDatabaseProductName());
                        System.out.println("    - Driver: " + connection.getMetaData().getDriverName());
                        System.out.println("    - URL: " + connection.getMetaData().getURL());
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("[✗] LỖI KẾT NỐI DATABASE:");
            System.err.println("    Chi tiết: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đóng Hibernate để giải phóng tài nguyên
            HibernateUtil.shutdown();
            System.out.println("\n========== DATABASE CONNECTION TEST END ==========");
        }
    }
}