package com.tuyensinh.repository;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.Nganh;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class NganhRepository {

    public void save(Nganh nganh) {

    Transaction tx = null;
    Session session = null;

    try {

        session = HibernateUtil
                .getSessionFactory()
                .openSession();

        tx = session.beginTransaction();

        session.persist(nganh);

        tx.commit();

    } catch (Exception e) {

        if (tx != null && tx.isActive()) {
            tx.rollback();
        }

        e.printStackTrace();

    } finally {

        if (session != null && session.isOpen()) {
            session.close();
        }
    }
}
                                

    public Optional<Nganh> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Nganh result = session.createQuery(
                    "from Nganh n where n.id = :id and n.status = 'active'",
                    Nganh.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(result);
        }
    }

    public List<Nganh> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Nganh n where n.status = 'active'", Nganh.class).list();
        }
    }

    public Map<String, Long> countNguyenVongByMaNganh() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<?> rawRows = session.createNativeQuery("""
                    SELECT n.manganh, COUNT(nv.idnv) AS so_luong_dang_ky
                    FROM xt_nganh n
                    LEFT JOIN xt_nguyenvongxettuyen nv
                        ON nv.nv_manganh = n.manganh
                       AND LOWER(COALESCE(nv.status, 'active')) = 'active'
                    WHERE LOWER(COALESCE(n.status, 'active')) = 'active'
                    GROUP BY n.manganh
                    """).list();

            Map<String, Long> result = new LinkedHashMap<>();
            for (Object rowObj : rawRows) {
                Object[] row = (Object[]) rowObj;
                String maNganh = row[0] == null ? "" : row[0].toString();
                Number countValue = row[1] instanceof Number ? (Number) row[1] : null;
                result.put(maNganh, countValue == null ? 0L : countValue.longValue());
            }
            return result;
        }
    }

    public Optional<Nganh> findByMaNganh(String manganh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Nganh result = session.createQuery(
                    "from Nganh n where n.manganh = :manganh and n.status = 'active'",
                    Nganh.class)
                    .setParameter("manganh", manganh)
                    .setMaxResults(1)
                    .uniqueResult();

            return Optional.ofNullable(result);
        }
    }

    public Nganh update(Nganh nganh) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Nganh merged = (Nganh) session.merge(nganh);
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
            Nganh existing = session.get(Nganh.class, id);

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

    // ================= IMPORT EXCEL =================

    /**
     * Excel format:
     * 0 manganh
     * 1 tennganh
     * 2 tohopgoc
     * 3 chitieu
     * 4 diemsan
     * 5 diemtrungtuyen
     * 6 tuyenthang
     * 7 dgnl
     * 8 thpt
     * 9 vsat
     * 10 sl_xtt
     * 11 sl_dgnl
     * 12 sl_vsat
     * 13 sl_thpt
     */
    public List<Nganh> importFromExcel(String filePath) throws IOException {

        List<Nganh> list = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {

                if (row.getRowNum() == 0 || isRowEmpty(row)) {
                    continue;
                }

                try {
                    Nganh nganh = parseRow(row);
                    save(nganh);
                    list.add(nganh);
                } catch (Exception ex) {
                    System.err.println("Import lỗi dòng "
                            + (row.getRowNum() + 1));
                }
            }
        }
        return list;
    }

    private Nganh parseRow(Row row) {
        return Nganh.builder()
                .manganh(getString(row, 0))
                .tennganh(getString(row, 1))
                .nTohopgoc(getString(row, 2))
                .nChitieu(getInt(row, 3))
                .nDiemsan(getDecimal(row, 4))
                .nDiemtrungtuyen(getDecimal(row, 5))
                .nTuyenthang(getString(row, 6))
                .nDgnl(getString(row, 7))
                .nThpt(getString(row, 8))
                .nVsat(getString(row, 9))
                .slXtt(getInt(row, 10))
                .slDgnl(getInt(row, 11))
                .slVsat(getInt(row, 12))
                .slThpt(getInt(row, 13))
                .build();
    }

    private String getString(Row row, int i) {
        Cell cell = row.getCell(i);
        return cell == null ? null : cell.toString().trim();
    }

    private Integer getInt(Row row, int i) {
        Cell cell = row.getCell(i);
        if (cell == null) return null;
        return (int) cell.getNumericCellValue();
    }

    private BigDecimal getDecimal(Row row, int i) {
        Cell cell = row.getCell(i);
        if (cell == null) return null;
        return new BigDecimal(cell.getNumericCellValue());
    }

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < 14; i++) {
            if (row.getCell(i) != null) return false;
        }
        return true;
    }

    /* KHÔNG DÙNG
    private void saveWithoutTransaction(Nganh nganh) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(nganh);
            tx.commit();
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }
    */

    private void rollbackQuietly(Transaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }
}
