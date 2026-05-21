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
            return session.createQuery("from User u where UPPER(u.status) != 'INACTIVE' order by u.id desc", User.class).list();
        }
    }

    public List<User> searchByKeyword(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "from User u where UPPER(u.status) != 'INACTIVE' and " +
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
                    "from User u where u.id = :id",
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
            // Load managed entity to avoid detached cascade issues with Role→permissions
            User managed = session.get(User.class, user.getId());
            if (managed == null) {
                tx.rollback();
                throw new RuntimeException("Không tìm thấy người dùng với ID: " + user.getId());
            }
            // Copy only User's own fields (avoid touching the Role relationship)
            managed.setUsername(user.getUsername());
            managed.setEmail(user.getEmail());
            managed.setPassword(user.getPassword());
            managed.setFullName(user.getFullName());
            managed.setPhoneNumber(user.getPhoneNumber());
            managed.setAvatarUrl(user.getAvatarUrl());
            managed.setStatus(user.getStatus());
            managed.setIdRoleValue(user.getIdRoleValue());
            managed.setStudentCccd(user.getStudentCccd());
            tx.commit();
            return managed;
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
            // XÓA MỀM TÀI KHOẢN VÀ THÍ SINH (NẾU CÓ)
            // ==========================================
            String cccd = existing.getStudentCccd();
            if (cccd != null && !cccd.trim().isEmpty()) {
                com.tuyensinh.model.ThiSinh thiSinh = session.createQuery("from ThiSinh t where t.cccd = :cccd", com.tuyensinh.model.ThiSinh.class)
                        .setParameter("cccd", cccd)
                        .setMaxResults(1)
                        .uniqueResult();
                if (thiSinh != null) {
                    thiSinh.setStatus("INACTIVE");
                    session.merge(thiSinh);
                }
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
                    "WHERE UPPER(u.status) != 'INACTIVE' AND u.username = :username";
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