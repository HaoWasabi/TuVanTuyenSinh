package com.tuyensinh.repository;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.NguyenVong;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class NguyenVongRepository {

    public NguyenVong save(NguyenVong nguyenVong) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(nguyenVong);
            tx.commit();
            return nguyenVong;
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }

    public Optional<NguyenVong> findById(Integer idnv) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(NguyenVong.class, idnv));
        }
    }

    public List<NguyenVong> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from NguyenVong", NguyenVong.class).list();
        }
    }

    // Hàm lấy nguyện vọng theo CCCD (Rất cần cho logic xét tuyển sắp tới)
    public List<NguyenVong> findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from NguyenVong n where n.nnCccd = :cccd order by n.nvTt asc", NguyenVong.class)
                    .setParameter("cccd", cccd)
                    .list();
        }
    }

    public NguyenVong update(NguyenVong nguyenVong) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            NguyenVong merged = (NguyenVong) session.merge(nguyenVong);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }

    public boolean deleteById(Integer idnv) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            NguyenVong existing = session.get(NguyenVong.class, idnv);
            if (existing == null) {
                tx.commit();
                return false;
            }
            session.remove(existing);
            tx.commit();
            return true;
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }

    private void rollbackQuietly(Transaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }
}