package com.tuyensinh.repository;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.DiemCong;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DiemCongRepository {

    public DiemCong save(DiemCong DiemCong) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(DiemCong);
            tx.commit();
            return DiemCong;
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }

    public Optional<DiemCong> findById(Integer idnv) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(DiemCong.class, idnv));
        }
    }

    public List<DiemCong> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from DiemCong", DiemCong.class).list();
        }
    }

    // Hàm lấy nguyện vọng theo CCCD (Rất cần cho logic xét tuyển sắp tới)
    public List<DiemCong> findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from DiemCong n where n.nnCccd = :cccd order by n.nvTt asc", DiemCong.class)
                    .setParameter("cccd", cccd)
                    .list();
        }
    }

    public DiemCong update(DiemCong DiemCong) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            DiemCong merged = (DiemCong) session.merge(DiemCong);
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
            DiemCong existing = session.get(DiemCong.class, idnv);
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