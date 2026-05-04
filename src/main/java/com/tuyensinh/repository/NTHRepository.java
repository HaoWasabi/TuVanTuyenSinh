package com.tuyensinh.repository;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.NganhToHop;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class NTHRepository {

    public NganhToHop save(NganhToHop majors) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(majors);
            tx.commit();
            return majors;
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }

    public Optional<NganhToHop> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NganhToHop result = session.createQuery("from NganhToHop m where m.id = :id and m.status = 'active'", NganhToHop.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(result);
        }
    }

    public List<NganhToHop> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from NganhToHop m where m.status = 'active'", NganhToHop.class).list();
        }
    }

    public Optional<NganhToHop> findByTbKeys(String tbKeys) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NganhToHop result = session.createQuery("from NganhToHop m where m.tbKeys = :tbKeys and m.status = 'active'", NganhToHop.class)
                    .setParameter("tbKeys", tbKeys)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(result);
        }
    }

    public NganhToHop update(NganhToHop majors) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            NganhToHop merged = (NganhToHop) session.merge(majors);
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
            NganhToHop existing = session.get(NganhToHop.class, id);
            if (existing == null) {
                tx.commit();
                return false;
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

    private void rollbackQuietly(Transaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }
}
