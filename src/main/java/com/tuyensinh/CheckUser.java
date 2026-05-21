package com.tuyensinh;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.User;
import org.hibernate.Session;

public class CheckUser {
    public static void main(String[] args) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User u = session.createQuery("from User where username = 'giamthi1'", User.class).uniqueResult();
            if (u != null) {
                System.out.println("Status: '" + u.getStatus() + "'");
                System.out.println("Password: '" + u.getPassword() + "'");
                System.out.println("Role ID: " + u.getIdRoleValue());
            } else {
                System.out.println("STILL NOT FOUND");
            }
        }
        System.exit(0);
    }
}
