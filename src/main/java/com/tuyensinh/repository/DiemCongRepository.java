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
            DiemCong result = session.createQuery(
                    "from DiemCong d where d.iddiemcong = :id and d.status = 'active'", DiemCong.class)
                    .setParameter("id", id)
                    .uniqueResult();
            return Optional.ofNullable(result);
        }
    }

    public List<DiemCong> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from DiemCong d where d.status = 'active'", DiemCong.class).list();
        }
    }

    // Hàm lấy điểm cộng theo CCCD
    public List<DiemCong> findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from DiemCong d where d.tsCccd = :cccd and d.status = 'active'", DiemCong.class)
                    .setParameter("cccd", cccd)
                    .list();
        }
    }

    public boolean add(DiemCong dc) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Hibernate sẽ tự động map object DiemCong thành lệnh INSERT SQL
            session.persist(dc);

            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Hoàn tác nếu có lỗi (ví dụ: trùng Keys)
            }
            e.printStackTrace();
            return false;
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
        Transaction tx = null;

        // Mở FileInputStream, Workbook và Session trong cùng 1 khối try-with-resources
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis);
             Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction(); // Chỉ mở 1 Transaction duy nhất
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0 || isRowEmpty(row)) continue;

                try {
                    DiemCong diemCong = parseRow(row);
                    session.persist(diemCong); // Đẩy vào bộ nhớ đệm của Hibernate
                    list.add(diemCong);

                    // Giải phóng bộ nhớ mỗi 50 dòng (Best practice cho dữ liệu lớn)
                    if (list.size() % 50 == 0) {
                        session.flush();
                        session.clear();
                    }
                } catch (Exception ex) {
                    System.err.println("Import lỗi dòng " + (row.getRowNum() + 1) + ": " + ex.getMessage());
                }
            }
            tx.commit(); // Ghi toàn bộ dữ liệu xuống Database trong 1 lần
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
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