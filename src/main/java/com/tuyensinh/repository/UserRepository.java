package com.tuyensinh.repository;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

public class UserRepository {

    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User u where u.status = 'active' order by u.id desc", User.class).list();
        }
    }

    public List<User> searchByKeyword(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "from User u where u.status = 'ACTIVE' and " +
                    "(lower(u.username) like :kw " +
                    "or lower(u.email) like :kw " +
                    "or lower(u.fullName) like :kw) " +
                    "order by u.id desc";
            return session.createQuery(hql, User.class)
                    .setParameter("kw", "%" + keyword.toLowerCase() + "%")
                    .list();
        }
    }

    public User save(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            return user;
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }

    public Optional<User> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User result = session.createQuery(
                    "from User u where u.id = :id and u.status = 'active'",
                    User.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(result);
        }
    }

    public User update(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User merged = (User) session.merge(user);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }

    public boolean deleteById(Integer id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User existing = session.get(User.class, id);
            if (existing == null) {
                tx.commit();
                return false;
            }
            // ==========================================
            // THÊM LOGIC CHẶN XÓA TÀI KHOẢN THÍ SINH
            // ==========================================
            String cccd = existing.getStudentCccd();
            if (cccd != null && !cccd.trim().isEmpty()) {
                tx.commit();
                // Ném ra ngoại lệ để báo cho tầng UI biết lý do không xóa được
                throw new RuntimeException("Không được phép xóa tài khoản của thí sinh!");
                // Hoặc nếu hệ thống của bạn không bắt Exception, bạn có thể return false;
            }
            // CHUYỂN SANG XÓA MỀM
            existing.setStatus("INACTIVE");
            session.merge(existing);
            tx.commit();
            return true;
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }

    public Optional<User> findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Thay JOIN FETCH bằng LEFT JOIN FETCH để tránh mất dữ liệu nếu Role bị lỗi logic
            String hql = "SELECT DISTINCT u FROM User u " +
                    "LEFT JOIN FETCH u.role r " +
                    "LEFT JOIN FETCH r.permissions " +
                    "WHERE u.status = 'ACTIVE' AND u.username = :username";
            return session.createQuery(hql, User.class)
                    .setParameter("username", username)
                    .uniqueResultOptional();
        }
    }

    public void updateLastLogin(Integer userId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user != null) {
                user.setLastLogin(LocalDateTime.now());
                session.merge(user);
            }
            tx.commit();
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }

    private void rollbackQuietly(Transaction tx) {
        try {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
        } catch (Exception e) {
            // Bỏ qua lỗi kết nối đóng khi rollback để nhường chỗ in ra lỗi SQL gốc
        }
    }
}