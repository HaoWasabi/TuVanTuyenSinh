package com.tuyensinh.repository;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.DiemThi;
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

    // Hàm lấy điểm thi theo CCCD
    public List<DiemThi> findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from DiemThi d where d.cccd = :cccd", DiemThi.class)
                    .setParameter("cccd", cccd)
                    .list();
        }
    }

    public boolean add(DiemThi dt) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Hibernate sẽ tự động map object NguyenVong thành lệnh INSERT SQL
            session.save(dt);

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

    // ================= IMPORT EXCEL =================

    /**
     * Excel format:
     * 0 cccd
     * 1 sobaodanh
     * 2 d_phuongthuc
     * 3 TO (toan)
     * 4 LI (vatLi)
     * 5 HO (hoaHoc)
     * 6 SI (sinhHoc)
     * 7 SU (lichSu)
     * 8 DI (diaLi)
     * 9 VA (nguVan)
     * 10 N1_THI
     * 11 N1_CC
     * 12 CNCN
     * 13 CNNN
     * 14 TI (tinHoc)
     * 15 KTPL
     * 16 NL1
     * 17 NK1
     * 18 NK2
     */
    public List<DiemThi> importFromExcel(String filePath) throws IOException {
        List<DiemThi> list = new ArrayList<>();
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
                    DiemThi diemThi = parseRow(row);
                    session.persist(diemThi); // Đẩy vào bộ nhớ đệm của Hibernate
                    list.add(diemThi);

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

    private DiemThi parseRow(Row row) {
        return DiemThi.builder()
                .cccd(getString(row, 0))
                .sobaodanh(getString(row, 1))
                .dPhuongthuc(getString(row, 2))
                .toan(getDecimal(row, 3))
                .vatLi(getDecimal(row, 4))
                .hoaHoc(getDecimal(row, 5))
                .sinhHoc(getDecimal(row, 6))
                .lichSu(getDecimal(row, 7))
                .diaLi(getDecimal(row, 8))
                .nguVan(getDecimal(row, 9))
                .n1Thi(getDecimal(row, 10))
                .n1Cc(getDecimal(row, 11))
                .cncn(getDecimal(row, 12))
                .cnnn(getDecimal(row, 13))
                .tinHoc(getDecimal(row, 14))
                .ktpl(getDecimal(row, 15))
                .nl1(getDecimal(row, 16))
                .nk1(getDecimal(row, 17))
                .nk2(getDecimal(row, 18))
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
        for (int i = 0; i < 19; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }

    // ================= THỐNG KÊ ĐIỂM =================

    /**
     * Thống kê điểm trung bình, min, max, số lượng theo môn
     * @param mon Tên môn: toan, vatLi, hoaHoc, sinhHoc, lichSu, diaLi, nguVan, tinHoc, ktpl, n1Thi, n1Cc, cncn, cnnn, nl1, nk1, nk2
     * @return Object[] {avg, min, max, count}
     */
    public Object[] thongKeDiemTheoMon(String mon) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT AVG(d." + mon + "), MIN(d." + mon + "), MAX(d." + mon + "), COUNT(d." + mon + ") FROM DiemThi d WHERE d." + mon + " IS NOT NULL";
            return session.createQuery(hql, Object[].class).uniqueResult();
        }
    }

    /**
     * Thống kê số lượng thí sinh theo khoảng điểm của một môn
     * @param mon Tên môn
     * @param tuDiem Điểm bắt đầu
     * @param denDiem Điểm kết thúc
     * @return Số lượng thí sinh
     */
    public Long demTheoKhoangDiem(String mon, BigDecimal tuDiem, BigDecimal denDiem) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(d) FROM DiemThi d WHERE d." + mon + " >= :tuDiem AND d." + mon + " <= :denDiem";
            return session.createQuery(hql, Long.class)
                    .setParameter("tuDiem", tuDiem)
                    .setParameter("denDiem", denDiem)
                    .uniqueResult();
        }
    }

    /**
     * Thống kê theo phương thức xét tuyển
     * @return List của Object[] {phuongThuc, soLuong}
     */
    public List<Object[]> thongKeSoLuongTheoPhuongThuc() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT d.dPhuongthuc, COUNT(d) FROM DiemThi d GROUP BY d.dPhuongthuc";
            return session.createQuery(hql, Object[].class).list();
        }
    }

    /**
     * Thống kê điểm trung bình tất cả các môn
     * @return Object[] {avgToan, avgLy, avgHoa, avgSinh, avgSu, avgDia, avgVan, avgTin, avgKtpl}
     */
    public Object[] thongKeDiemTrungBinhTatCaMon() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT AVG(d.toan), AVG(d.vatLi), AVG(d.hoaHoc), AVG(d.sinhHoc), " +
                        "AVG(d.lichSu), AVG(d.diaLi), AVG(d.nguVan), AVG(d.tinHoc), AVG(d.ktpl) FROM DiemThi d";
            return session.createQuery(hql, Object[].class).uniqueResult();
        }
    }

    /**
     * Thống kê phân bố điểm theo môn (chia thành các khoảng 0-2, 2-4, 4-6, 6-8, 8-10)
     * @param mon Tên môn
     * @return List của Object[] {khoangDiem, soLuong}
     */
    public List<Object[]> thongKePhanBoTheoMon(String mon) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT " +
                    "CASE " +
                    "  WHEN d." + mon + " < 2 THEN '0-2' " +
                    "  WHEN d." + mon + " < 4 THEN '2-4' " +
                    "  WHEN d." + mon + " < 6 THEN '4-6' " +
                    "  WHEN d." + mon + " < 8 THEN '6-8' " +
                    "  ELSE '8-10' " +
                    "END, COUNT(d) " +
                    "FROM DiemThi d WHERE d." + mon + " IS NOT NULL " +
                    "GROUP BY CASE " +
                    "  WHEN d." + mon + " < 2 THEN '0-2' " +
                    "  WHEN d." + mon + " < 4 THEN '2-4' " +
                    "  WHEN d." + mon + " < 6 THEN '4-6' " +
                    "  WHEN d." + mon + " < 8 THEN '6-8' " +
                    "  ELSE '8-10' END " +
                    "ORDER BY 1";
            return session.createQuery(hql, Object[].class).list();
        }
    }
}