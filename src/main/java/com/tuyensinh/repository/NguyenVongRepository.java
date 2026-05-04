package com.tuyensinh.repository;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.NguyenVong;
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

    public Optional<NguyenVong> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NguyenVong result = session.createQuery(
                    "from NguyenVong n where n.id = :id and n.status = 'active'",
                    NguyenVong.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(result);
        }
    }

    public List<NguyenVong> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from NguyenVong n where n.status = 'active'", NguyenVong.class).list();
        }
    }

    // Hàm lấy nguyện vọng theo CCCD
    public List<NguyenVong> findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from NguyenVong n where n.nnCccd = :cccd and n.status = 'active' order by n.nvTt asc", NguyenVong.class)
                    .setParameter("cccd", cccd)
                    .list();
        }
    }

    // Hàm lấy danh sách nguyện vọng theo ngành, sắp xếp điểm từ cao xuống thấp
    public List<NguyenVong> findByMaNganh(String maNganh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from NguyenVong n where n.nvManganh = :maNganh and n.status = 'active' order by n.diemXettuyen desc, n.nvTt asc", NguyenVong.class)
                    .setParameter("maNganh", maNganh)
                    .list();
        }
    }

    public boolean add(NguyenVong nv) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Hibernate sẽ tự động map object NguyenVong thành lệnh INSERT SQL
            session.save(nv);

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

    public boolean deleteById(Integer id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            NguyenVong existing = session.get(NguyenVong.class, id);
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
     * 0 nn_cccd
     * 1 nv_manganh
     * 2 nv_tt
     * 3 diem_thxt
     * 4 diem_utqd
     * 5 diem_cong
     * 6 diem_xettuyen
     * 7 nv_ketqua
     * 8 nv_keys
     * 9 tt_phuongthuc
     * 10 tt_thm
     */
    public List<NguyenVong> importFromExcel(String filePath) throws IOException {
        List<NguyenVong> list = new ArrayList<>();
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
                    NguyenVong nguyenVong = parseRow(row);
                    session.persist(nguyenVong); // Đẩy vào bộ nhớ đệm của Hibernate
                    list.add(nguyenVong);

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

    private NguyenVong parseRow(Row row) {
        return NguyenVong.builder()
                .nnCccd(getString(row, 0))
                .nvManganh(getString(row, 1))
                .nvTt(getInt(row, 2))
                .diemThxt(getDecimal(row, 3))
                .diemUtqd(getDecimal(row, 4))
                .diemCong(getDecimal(row, 5))
                .diemXettuyen(getDecimal(row, 6))
                .nvKetqua(getString(row, 7))
                .nvKeys(getString(row, 8))
                .ttPhuongthuc(getString(row, 9))
                .ttThm(getString(row, 10))
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

    private Integer getInt(Row row, int i) {
        Cell cell = row.getCell(i);
        if (cell == null || cell.getCellType() == CellType.BLANK) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        String val = cell.toString().trim();
        if (val.isEmpty()) return null;
        return Integer.parseInt(val);
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
        for (int i = 0; i < 11; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }
}