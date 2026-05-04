package com.tuyensinh.repository;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.NganhToHop;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    /**
     * Import danh sách NganhToHop từ Excel.
        * File Excel không chứa cột ID, DB sẽ tự sinh ID.
        * Định dạng cột: manganh, matohop, th_mon1, hsmon1, th_mon2, hsmon2,
        * th_mon3, hsmon3, tb_keys, dolech.
     */
    public List<NganhToHop> importFromExcel(String filePath) throws IOException {
        List<NganhToHop> importedList = new ArrayList<>();

        Transaction tx = null;
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis);
             Session session = HibernateUtil.getSessionFactory().openSession()) {

            Sheet sheet = workbook.getSheetAt(0);
            boolean hasIdColumn = detectIdColumn(sheet);
            int rowStart = 1;

            tx = session.beginTransaction();
            int processedCount = 0;

            for (Row row : sheet) {
                if (row.getRowNum() < rowStart) {
                    continue;
                }
                if (isRowEmpty(row)) {
                    continue;
                }

                try {
                    NganhToHop nganhToHop = parseExcelRow(row, hasIdColumn);
                    upsertByTbKeys(session, nganhToHop);
                    importedList.add(nganhToHop);

                    processedCount++;
                    if (processedCount % 50 == 0) {
                        session.flush();
                        session.clear();
                    }
                } catch (Exception ex) {
                    System.err.println("Lỗi import dòng " + (row.getRowNum() + 1) + ": " + ex.getMessage());
                }
            }

            tx.commit();
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        }

        return importedList;
    }

    private boolean detectIdColumn(Sheet sheet) {
        Row header = sheet.getRow(0);
        if (header == null) {
            return false;
        }
        String firstHeader = safeText(getCellAsString(header, 0)).toLowerCase();
        return firstHeader.contains("id");
    }

    private NganhToHop parseExcelRow(Row row, boolean hasIdColumn) {
        NganhToHop item = new NganhToHop();

        item.setId(null);
        int offset = hasIdColumn ? 1 : 0;
        if (hasIdColumn) {
            item.setId(getCellAsInteger(row, 0));
        }

        item.setMaNganh(truncateString(getCellAsString(row, offset + 0), 45));
        item.setMaToHop(truncateString(getCellAsString(row, offset + 1), 45));
        item.setThMon1(truncateString(getCellAsString(row, offset + 2), 10));
        item.setHsMon1(getCellAsByte(row, offset + 3));
        item.setThMon2(truncateString(getCellAsString(row, offset + 4), 10));
        item.setHsMon2(getCellAsByte(row, offset + 5));
        item.setThMon3(truncateString(getCellAsString(row, offset + 6), 10));
        item.setHsMon3(getCellAsByte(row, offset + 7));
        item.setTbKeys(truncateString(getCellAsString(row, offset + 8), 45));

        String mon1 = normalizeSubject(item.getThMon1());
        String mon2 = normalizeSubject(item.getThMon2());
        String mon3 = normalizeSubject(item.getThMon3());
        String maToHop = safeText(item.getMaToHop()).toLowerCase();
        String tbKeys = safeText(item.getTbKeys()).toLowerCase();

        item.setN1(maToHop.contains("n1") || tbKeys.contains("n1"));
        item.setTo(hasSubject(mon1, mon2, mon3, "toan"));
        item.setLi(hasSubject(mon1, mon2, mon3, "vatly"));
        item.setHo(hasSubject(mon1, mon2, mon3, "hoahoc"));
        item.setSi(hasSubject(mon1, mon2, mon3, "sinhhoc"));
        item.setVa(hasSubject(mon1, mon2, mon3, "nguvan"));
        item.setSu(hasSubject(mon1, mon2, mon3, "lichsu"));
        item.setDi(hasSubject(mon1, mon2, mon3, "dialy"));
        item.setTi(hasSubject(mon1, mon2, mon3, "tienganh"));
        item.setKtpl(hasSubject(mon1, mon2, mon3, "kinhtephapluat"));

        boolean hasKnownSubject = Boolean.TRUE.equals(item.getTo()) || Boolean.TRUE.equals(item.getLi())
            || Boolean.TRUE.equals(item.getHo()) || Boolean.TRUE.equals(item.getSi())
            || Boolean.TRUE.equals(item.getVa()) || Boolean.TRUE.equals(item.getSu())
            || Boolean.TRUE.equals(item.getDi()) || Boolean.TRUE.equals(item.getTi())
            || Boolean.TRUE.equals(item.getKtpl());
        item.setKhac(!hasKnownSubject);
        item.setDoLech(getCellAsBigDecimal(row, offset + 9));

        return item;
    }

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
        return Integer.valueOf(value);
    }

    private Byte getCellAsByte(Row row, int cellIndex) {
        String value = getCellAsString(row, cellIndex);
        if (value == null || value.isEmpty()) {
            return null;
        }
        if (value.endsWith(".0")) {
            value = value.substring(0, value.length() - 2);
        }
        return Byte.valueOf(value);
    }

    private BigDecimal getCellAsBigDecimal(Row row, int cellIndex) {
        String value = getCellAsString(row, cellIndex);
        if (value == null || value.isEmpty()) {
            return null;
        }
        if (value.endsWith(".0")) {
            value = value.substring(0, value.length() - 2);
        }
        return new BigDecimal(value);
    }

    private boolean getCellAsBoolean(Row row, int cellIndex) {
        String value = getCellAsString(row, cellIndex);
        if (value == null) {
            return false;
        }
        String text = value.trim().toLowerCase();
        return "1".equals(text)
                || "true".equals(text)
                || "yes".equals(text)
                || "y".equals(text)
                || "co".equals(text)
                || "có".equals(text)
                || "x".equals(text);
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int i = 0; i < Math.max(10, row.getLastCellNum()); i++) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                DataFormatter formatter = new DataFormatter();
                String text = formatter.formatCellValue(cell);
                if (text != null && !text.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private String truncateString(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        if (value.length() > maxLength) {
            return value.substring(0, maxLength);
        }
        return value;
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private String normalizeSubject(String value) {
        if (value == null) {
            return "";
        }
        String text = value.toLowerCase();
        text = text.replace("á", "a").replace("à", "a").replace("ả", "a").replace("ã", "a").replace("ạ", "a");
        text = text.replace("ă", "a").replace("ắ", "a").replace("ằ", "a").replace("ẳ", "a").replace("ẵ", "a").replace("ặ", "a");
        text = text.replace("â", "a").replace("ấ", "a").replace("ầ", "a").replace("ẩ", "a").replace("ẫ", "a").replace("ậ", "a");
        text = text.replace("é", "e").replace("è", "e").replace("ẻ", "e").replace("ẽ", "e").replace("ẹ", "e");
        text = text.replace("ê", "e").replace("ế", "e").replace("ề", "e").replace("ể", "e").replace("ễ", "e").replace("ệ", "e");
        text = text.replace("í", "i").replace("ì", "i").replace("ỉ", "i").replace("ĩ", "i").replace("ị", "i");
        text = text.replace("ó", "o").replace("ò", "o").replace("ỏ", "o").replace("õ", "o").replace("ọ", "o");
        text = text.replace("ô", "o").replace("ố", "o").replace("ồ", "o").replace("ổ", "o").replace("ỗ", "o").replace("ộ", "o");
        text = text.replace("ơ", "o").replace("ớ", "o").replace("ờ", "o").replace("ở", "o").replace("ỡ", "o").replace("ợ", "o");
        text = text.replace("ú", "u").replace("ù", "u").replace("ủ", "u").replace("ũ", "u").replace("ụ", "u");
        text = text.replace("ư", "u").replace("ứ", "u").replace("ừ", "u").replace("ử", "u").replace("ữ", "u").replace("ự", "u");
        text = text.replace("ý", "y").replace("ỳ", "y").replace("ỷ", "y").replace("ỹ", "y").replace("ỵ", "y");
        text = text.replace("đ", "d");
        return text.replaceAll("[^a-z0-9]", "");
    }

    private boolean hasSubject(String mon1, String mon2, String mon3, String key) {
        return mon1.contains(key) || mon2.contains(key) || mon3.contains(key);
    }

    private void saveWithoutTransaction(NganhToHop majors) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(majors);
            tx.commit();
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }

    private void upsertByTbKeys(Session session, NganhToHop item) {
        if (item.getTbKeys() == null || item.getTbKeys().trim().isEmpty()) {
            session.persist(item);
            return;
        }

        Optional<NganhToHop> existing = session.createQuery(
                        "from NganhToHop m where m.tbKeys = :tbKeys", NganhToHop.class)
                .setParameter("tbKeys", item.getTbKeys())
                .setMaxResults(1)
                .uniqueResultOptional();
        if (existing.isPresent()) {
            item.setId(existing.get().getId());
            session.merge(item);
        } else {
            session.persist(item);
        }
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
