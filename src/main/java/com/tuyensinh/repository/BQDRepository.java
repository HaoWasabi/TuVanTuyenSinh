package com.tuyensinh.repository;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.BangQuyDoi;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BQDRepository {

    public BangQuyDoi save(BangQuyDoi bangQuyDoi) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(bangQuyDoi);
            tx.commit();
            return bangQuyDoi;
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }

    public Optional<BangQuyDoi> findById(Integer idqd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(BangQuyDoi.class, idqd));
        }
    }

    public List<BangQuyDoi> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from BangQuyDoi", BangQuyDoi.class).list();
        }
    }

    public Optional<BangQuyDoi> findByMaquydoi(String dMaquydoi) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BangQuyDoi result = session.createQuery("from BangQuyDoi b where b.dMaquydoi = :dMaquydoi", BangQuyDoi.class)
                    .setParameter("dMaquydoi", dMaquydoi)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(result);
        }
    }

    public Optional<BangQuyDoi> findByTohopAndMon(String dTohop, String dMon) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BangQuyDoi result = session.createQuery("from BangQuyDoi b where b.dTohop = :dTohop and b.dMon = :dMon", BangQuyDoi.class)
                    .setParameter("dTohop", dTohop)
                    .setParameter("dMon", dMon)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(result);
        }
    }

    public BangQuyDoi update(BangQuyDoi bangQuyDoi) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            BangQuyDoi merged = (BangQuyDoi) session.merge(bangQuyDoi);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }

    public boolean deleteById(Integer idqd) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            BangQuyDoi existing = session.get(BangQuyDoi.class, idqd);
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
