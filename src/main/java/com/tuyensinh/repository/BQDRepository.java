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
import org.apache.poi.ss.usermodel.DataFormatter;
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
            return Optional.ofNullable(session.get(BangQuyDoi.class, idqd));
        }
    }

    public List<BangQuyDoi> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from BangQuyDoi b where b.status = 'active'", BangQuyDoi.class).list();
        }
    }

    public Optional<BangQuyDoi> findByMaquydoi(String dMaquydoi) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BangQuyDoi result = session.createQuery("from BangQuyDoi b where b.dMaquydoi = :dMaquydoi and b.status = 'active'", BangQuyDoi.class)
                    .setParameter("dMaquydoi", dMaquydoi)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(result);
        }
    }

    public Optional<BangQuyDoi> findByTohopAndMon(String dTohop, String dMon) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BangQuyDoi result = session.createQuery("from BangQuyDoi b where b.dTohop = :dTohop and b.dMon = :dMon and b.status = 'active'", BangQuyDoi.class)
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

    /**
     * Import danh sách BangQuyDoi từ file Excel
     * Hỗ trợ 2 định dạng:
     * 1) Có ID: idqd, d_phuongthuc, d_tohop, d_mon, d_diema, d_diemb, d_diemc, d_diemd, d_maquydoi, d_phanvi
     * 2) Không ID: d_phuongthuc, d_tohop, d_mon, d_diema, d_diemb, d_diemc, d_diemd, d_maquydoi, d_phanvi
     * 
     * @param filePath Đường dẫn file Excel
     * @return Danh sách BangQuyDoi đã import
     * @throws IOException nếu không đọc được file
     */
    public List<BangQuyDoi> importFromExcel(String filePath) throws IOException {
        List<BangQuyDoi> importedList = new ArrayList<>();
        Transaction tx = null;
        
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis);
             Session session = HibernateUtil.getSessionFactory().openSession()) {
            
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            boolean hasIdColumn = detectIdColumn(sheet);
            int rowStart = 1; // Bỏ qua dòng header (dòng 0)
            int processedCount = 0;

            tx = session.beginTransaction();
            
            for (Row row : sheet) {
                if (row.getRowNum() < rowStart) {
                    continue; // Bỏ qua header
                }
                
                // Kiểm tra dòng trống
                if (isRowEmpty(row)) {
                    continue;
                }
                
                try {
                    BangQuyDoi bangQuyDoi = parseExcelRow(row, hasIdColumn);
                    upsertByMaquydoi(session, bangQuyDoi);
                    importedList.add(bangQuyDoi);

                    processedCount++;
                    if (processedCount % 50 == 0) {
                        session.flush();
                        session.clear();
                    }
                } catch (Exception ex) {
                    System.err.println("Lỗi import dòng " + (row.getRowNum() + 1) + ": " + ex.getMessage());
                    // Tiếp tục import các dòng khác
                }
            }

            tx.commit();
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
        
        return importedList;
    }

    private boolean detectIdColumn(Sheet sheet) {
        Row header = sheet.getRow(0);
        if (header == null) {
            return false;
        }
        String firstHeader = getCellAsString(header, 0);
        if (firstHeader == null) {
            return false;
        }
        return firstHeader.trim().toLowerCase().contains("id");
    }

    /**
     * Parse dữ liệu từ một dòng Excel
     */
    private BangQuyDoi parseExcelRow(Row row, boolean hasIdColumn) {
        BangQuyDoi bangQuyDoi = new BangQuyDoi();
        int offset = hasIdColumn ? 1 : 0;

        if (hasIdColumn) {
            bangQuyDoi.setIdqd(getCellAsInteger(row, 0));
        } else {
            bangQuyDoi.setIdqd(null);
        }
        
        bangQuyDoi.setDPhuongthuc(truncateString(getCellAsString(row, offset + 0), 45));
        bangQuyDoi.setDTohop(truncateString(getCellAsString(row, offset + 1), 45));
        bangQuyDoi.setDMon(truncateString(getCellAsString(row, offset + 2), 45));
        bangQuyDoi.setDDiema(getCellAsBigDecimal(row, offset + 3));
        bangQuyDoi.setDDiemb(getCellAsBigDecimal(row, offset + 4));
        bangQuyDoi.setDDiemc(getCellAsBigDecimal(row, offset + 5));
        bangQuyDoi.setDDiemd(getCellAsBigDecimal(row, offset + 6));
        bangQuyDoi.setDMaquydoi(truncateString(getCellAsString(row, offset + 7), 255));
        bangQuyDoi.setDPhanvi(truncateString(getCellAsString(row, offset + 8), 255));
        
        return bangQuyDoi;
    }

    private void upsertByMaquydoi(Session session, BangQuyDoi bangQuyDoi) {
        String maQuyDoi = bangQuyDoi.getDMaquydoi();
        if (maQuyDoi == null || maQuyDoi.trim().isEmpty()) {
            session.persist(bangQuyDoi);
            return;
        }

        Optional<BangQuyDoi> existing = session.createQuery(
                        "from BangQuyDoi b where b.dMaquydoi = :dMaquydoi and b.status = 'active'", BangQuyDoi.class)
                .setParameter("dMaquydoi", maQuyDoi)
                .setMaxResults(1)
                .uniqueResultOptional();

        if (existing.isPresent()) {
            bangQuyDoi.setIdqd(existing.get().getIdqd());
            session.merge(bangQuyDoi);
        } else {
            session.persist(bangQuyDoi);
        }
    }

    /**
     * Lấy giá trị cell dạng String
     */
    private String getCellAsString(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return null;
        }
        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell);
        return value == null ? null : value.trim();
    }

    private Integer getCellAsInteger(Row row, int cellIndex) {
        String value = getCellAsString(row, cellIndex);
        if (value == null || value.isEmpty()) {
            return null;
        }
        if (value.endsWith(".0")) {
            value = value.substring(0, value.length() - 2);
        }
        return Integer.valueOf(value);
    }

    /**
     * Lấy giá trị cell dạng BigDecimal
     */
    private BigDecimal getCellAsBigDecimal(Row row, int cellIndex) {
        String value = getCellAsString(row, cellIndex);
        if (value == null || value.isEmpty()) {
            return null;
        }
        String normalized = value.replace(",", "");
        if (normalized.endsWith(".0")) {
            normalized = normalized.substring(0, normalized.length() - 2);
        }
        return new BigDecimal(normalized);
    }

    /**
     * Kiểm tra xem dòng có trống hay không
     */
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int i = 0; i < Math.max(9, row.getLastCellNum()); i++) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                String text = getCellAsString(row, i);
                if (text != null && !text.trim().isEmpty()) {
                    return false;
                }
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

    private void rollbackQuietly(Transaction tx) {
        if (tx == null) {
            return;
        }
        try {
            tx.rollback();
        } catch (Exception ignored) {
            // Keep original persistence exception as root cause.
        }
    }
}
