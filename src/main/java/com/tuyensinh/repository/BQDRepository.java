package com.tuyensinh.repository;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.BangQuyDoi;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
            BangQuyDoi result = session.createQuery(
                            "from BangQuyDoi b where b.idqd = :idqd and b.status = 'ACTIVE'", BangQuyDoi.class)
                    .setParameter("idqd", idqd)
                    .uniqueResult();
            return Optional.ofNullable(result);
        }
    }

    public List<BangQuyDoi> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from BangQuyDoi b where b.status = 'active' order by b.idqd desc", BangQuyDoi.class)
                    .list();
        }
    }

    public Optional<BangQuyDoi> findByMaquydoi(String dMaquydoi) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BangQuyDoi result = session.createQuery("from BangQuyDoi b where b.status = 'active' and b.dMaquydoi = :dMaquydoi", BangQuyDoi.class)
                    .setParameter("dMaquydoi", dMaquydoi)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(result);
        }
    }

    public Optional<BangQuyDoi> findByTohopAndMon(String dTohop, String dMon) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BangQuyDoi result = session.createQuery("from BangQuyDoi b where b.status = 'active' and b.dTohop = :dTohop and b.dMon = :dMon", BangQuyDoi.class)
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

    /**
     * Import danh sách BangQuyDoi từ file Excel
     * Định dạng Excel: 
     *   - Dòng 1: Header (bỏ qua)
     *   - Từ dòng 2 trở đi: dữ liệu
     *   - Cột: phuongThuc, toHop, mon, dieA, dieB, dieC, dieD, maQuyDoi, phanvi
     * 
     * @param filePath Đường dẫn file Excel
     * @return Danh sách BangQuyDoi đã import
     * @throws IOException nếu không đọc được file
     */
    public List<BangQuyDoi> importFromExcel(String filePath) throws IOException {
        List<BangQuyDoi> importedList = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            int rowStart = 1; // Bỏ qua dòng header (dòng 0)
            
            for (Row row : sheet) {
                if (row.getRowNum() < rowStart) {
                    continue; // Bỏ qua header
                }
                
                // Kiểm tra dòng trống
                if (isRowEmpty(row)) {
                    continue;
                }
                
                try {
                    BangQuyDoi bangQuyDoi = parseExcelRow(row);
                    saveWithoutTransaction(bangQuyDoi);
                    importedList.add(bangQuyDoi);
                } catch (Exception ex) {
                    System.err.println("Lỗi import dòng " + (row.getRowNum() + 1) + ": " + ex.getMessage());
                    // Tiếp tục import các dòng khác
                }
            }
        }
        
        return importedList;
    }

    /**
     * Parse dữ liệu từ một dòng Excel
     * Cột: 0=phuongThuc, 1=toHop, 2=mon, 3=dieA, 4=dieB, 5=dieC, 6=dieD, 7=maQuyDoi, 8=phanvi
     */
    private BangQuyDoi parseExcelRow(Row row) {
        BangQuyDoi bangQuyDoi = new BangQuyDoi();
        
        bangQuyDoi.setDPhuongthuc(truncateString(getCellAsString(row, 0), 45));
        bangQuyDoi.setDTohop(truncateString(getCellAsString(row, 1), 45));
        bangQuyDoi.setDMon(truncateString(getCellAsString(row, 2), 45));
        bangQuyDoi.setDDiema(getCellAsBigDecimal(row, 3));
        bangQuyDoi.setDDiemb(getCellAsBigDecimal(row, 4));
        bangQuyDoi.setDDiemc(getCellAsBigDecimal(row, 5));
        bangQuyDoi.setDDiemd(getCellAsBigDecimal(row, 6));
        bangQuyDoi.setDMaquydoi(truncateString(getCellAsString(row, 7), 255));
        bangQuyDoi.setDPhanvi(truncateString(getCellAsString(row, 8), 255));
        
        return bangQuyDoi;
    }

    /**
     * Lấy giá trị cell dạng String
     */
    private String getCellAsString(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return null;
        }
        return cell.getStringCellValue().trim();
    }

    /**
     * Lấy giá trị cell dạng BigDecimal
     */
    private BigDecimal getCellAsBigDecimal(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return null;
        }
        try {
            return new BigDecimal(cell.getNumericCellValue());
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Kiểm tra xem dòng có trống hay không
     */
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int i = 0; i < 9; i++) { // Kiểm tra 9 cột
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType().toString().length() > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Truncate string nếu vượt quá độ dài tối đa
     */
    private String truncateString(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        if (value.length() > maxLength) {
            return value.substring(0, maxLength);
        }
        return value;
    }

    /**
     * Lưu BangQuyDoi không cần transaction (dùng cho batch import)
     */
    private void saveWithoutTransaction(BangQuyDoi bangQuyDoi) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(bangQuyDoi);
            tx.commit();
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
