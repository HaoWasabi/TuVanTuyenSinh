package com.tuyensinh.repository;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.DiemThi;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DiemThiRepository {

    public DiemThi save(DiemThi DiemThi) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(DiemThi);
            tx.commit();
            return DiemThi;
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }

    public Optional<DiemThi> findById(Integer idnv) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(DiemThi.class, idnv));
        }
    }

    public List<DiemThi> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from DiemThi", DiemThi.class).list();
        }
    }

    // Hàm lấy nguyện vọng theo CCCD (Rất cần cho logic xét tuyển sắp tới)
    public List<DiemThi> findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from DiemThi n where n.nnCccd = :cccd order by n.nvTt asc", DiemThi.class)
                    .setParameter("cccd", cccd)
                    .list();
        }
    }

    public DiemThi update(DiemThi DiemThi) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            DiemThi merged = (DiemThi) session.merge(DiemThi);
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
            DiemThi existing = session.get(DiemThi.class, idnv);
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