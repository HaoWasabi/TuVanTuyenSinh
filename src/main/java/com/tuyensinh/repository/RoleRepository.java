package com.tuyensinh.repository;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.Role;
import org.hibernate.Session;
import java.util.Optional;

public class RoleRepository {
    
    public Optional<Role> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Sử dụng JOIN FETCH để lấy quyền (permissions) ngay lập tức
            String hql = "SELECT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.id = :id";
            return session.createQuery(hql, Role.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }
    }
}