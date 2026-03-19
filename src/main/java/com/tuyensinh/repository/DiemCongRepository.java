package com.tuyensinh.repository;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.DiemCong;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DiemCongRepository {

    public DiemCong save(DiemCong diemCong) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(diemCong);
            tx.commit();
            return diemCong;
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }

    public Optional<DiemCong> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(DiemCong.class, id));
        }
    }

    public List<DiemCong> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from DiemCong", DiemCong.class).list();
        }
    }

    // Hàm lấy điểm cộng theo CCCD
    public List<DiemCong> findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from DiemCong d where d.tsCccd = :cccd", DiemCong.class)
                    .setParameter("cccd", cccd)
                    .list();
        }
    }

    public DiemCong update(DiemCong diemCong) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            DiemCong merged = (DiemCong) session.merge(diemCong);
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
            DiemCong existing = session.get(DiemCong.class, id);
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

    // ================= IMPORT EXCEL =================

    /**
     * Excel format:
     * 0 ts_cccd
     * 1 manganh
     * 2 matohop
     * 3 phuongthuc
     * 4 diemCC
     * 5 diemUtxt
     * 6 diemTong
     * 7 ghichu
     * 8 dc_keys
     */
    public List<DiemCong> importFromExcel(String filePath) throws IOException {

        List<DiemCong> list = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {

                if (row.getRowNum() == 0 || isRowEmpty(row)) {
                    continue;
                }

                try {
                    DiemCong diemCong = parseRow(row);
                    save(diemCong);
                    list.add(diemCong);
                } catch (Exception ex) {
                    System.err.println("Import lỗi dòng "
                            + (row.getRowNum() + 1) + ": " + ex.getMessage());
                }
            }
        }
        return list;
    }

    private DiemCong parseRow(Row row) {
        return DiemCong.builder()
                .tsCccd(getString(row, 0))
                .manganh(getString(row, 1))
                .matohop(getString(row, 2))
                .phuongthuc(getString(row, 3))
                .diemCC(getDecimal(row, 4))
                .diemUtxt(getDecimal(row, 5))
                .diemTong(getDecimal(row, 6))
                .ghichu(getString(row, 7))
                .dcKeys(getString(row, 8))
                .build();
    }

    private String getString(Row row, int i) {
        Cell cell = row.getCell(i);
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return cell.toString().trim();
    }

    private BigDecimal getDecimal(Row row, int i) {
        Cell cell = row.getCell(i);
        if (cell == null || cell.getCellType() == CellType.BLANK) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }
        String val = cell.toString().trim();
        if (val.isEmpty()) return null;
        return new BigDecimal(val);
    }

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < 9; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }
}