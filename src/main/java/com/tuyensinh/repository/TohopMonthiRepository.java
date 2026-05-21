package com.tuyensinh.repository;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.TohopMonthi;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TohopMonthiRepository {

    // ================= SAVE =================
    public void save(TohopMonthi entity) {

        Transaction tx = null;

        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();

        } catch (Exception e) {
            rollback(tx);
            throw e;
        }
    }

    // ================= FIND BY ID =================
    public Optional<TohopMonthi> findById(Integer id) {

        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            return Optional.ofNullable(
                    session.get(TohopMonthi.class, id));
        }
    }

    // ================= FIND ALL =================
    public List<TohopMonthi> findAll() {

        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            return session.createQuery(
                    "from TohopMonthi t where t.status = 'active'",
                    TohopMonthi.class
            ).list();
        }
    }

    // ================= FIND BY MATOHOP =================
    public Optional<TohopMonthi> findByMaTohop(String matohop) {

        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            TohopMonthi result = session.createQuery(
                    "from TohopMonthi t where t.matohop = :matohop and t.status = 'active'",
                    TohopMonthi.class)
                    .setParameter("matohop", matohop)
                    .setMaxResults(1)
                    .uniqueResult();

            return Optional.ofNullable(result);
        }
    }

    // ================= UPDATE =================
    public TohopMonthi update(TohopMonthi entity) {

        Transaction tx = null;

        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();
            TohopMonthi merged = session.merge(entity);
            tx.commit();

            return merged;

        } catch (Exception e) {
            rollback(tx);
            throw e;
        }
    }
    
    // ================= EXPORT EXCEL =================
        public void exportToExcel(String filePath) throws Exception {

            List<TohopMonthi> list = findAll();

            try (Workbook workbook = new XSSFWorkbook()) {

                Sheet sheet = workbook.createSheet("ToHopMonThi");

                // ===== HEADER =====
                Row header = sheet.createRow(0);

                header.createCell(0).setCellValue("matohop");
                header.createCell(1).setCellValue("mon1");
                header.createCell(2).setCellValue("mon2");
                header.createCell(3).setCellValue("mon3");
                header.createCell(4).setCellValue("tentohop");

                // ===== DATA =====
                int rowIndex = 1;

                for (TohopMonthi t : list) {

                    Row row = sheet.createRow(rowIndex++);

                    row.createCell(0).setCellValue(t.getMatohop());
                    row.createCell(1).setCellValue(t.getMon1());
                    row.createCell(2).setCellValue(t.getMon2());
                    row.createCell(3).setCellValue(t.getMon3());
                    row.createCell(4).setCellValue(t.getTentohop());
                }

                // auto size dòng 
                for (int i = 0; i < 5; i++) {
                    sheet.autoSizeColumn(i);
                }

                // ===== Viết FILE =====
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    workbook.write(fos);
                }
            }
        }
        private TohopMonthi parseRow(Row row) {

        return TohopMonthi.builder()
            .matohop(getString(row, 0))
            .mon1(getString(row, 1))
            .mon2(getString(row, 2))
            .mon3(getString(row, 3))
            .tentohop(getString(row, 4))
            .build();
        }

        private String getString(Row row, int i) {
            Cell cell = row.getCell(i);
            return cell == null ? null : cell.toString().trim();
        }

        private boolean isRowEmpty(Row row) {
            for (int i = 0; i < 5; i++) {
                if (row.getCell(i) != null) return false;
            }
            return true;
        }
        
        //
        // HÀM NÀY DÙNG CHO SAU NÀY TEAM FONTEND NHẬP FILE VÀO VÀ LƯU DỮ LIỆU
        //
        public void saveImported(List<TohopMonthi> list) {

    Transaction tx = null;

    try (Session session =
            HibernateUtil.getSessionFactory().openSession()) {

        tx = session.beginTransaction();

        for (TohopMonthi item : list) {

            // tìm theo mã tổ hợp
            TohopMonthi existing = session.createQuery(
                    "from TohopMonthi t where t.matohop = :ma and t.status = 'active'",
                    TohopMonthi.class)
                    .setParameter("ma", item.getMatohop())
                    .setMaxResults(1)
                    .uniqueResult();

            if (existing == null) {
                // ===== INSERT =====
                session.persist(item);
            } else {
                // ===== UPDATE =====
                existing.setMon1(item.getMon1());
                existing.setMon2(item.getMon2());
                existing.setMon3(item.getMon3());
                existing.setTentohop(item.getTentohop());

                session.merge(existing);
            }
        }

        tx.commit();

    } catch (Exception ex) {

        if (tx != null && tx.isActive()) {
            tx.rollback();
        }

        throw ex;
    }
}
        
            // ================= IMPORT EXCEL =================
            //CHỈ TRẢ VỀ DỮ LIỆU ĐỂ ĐỔ DỮ LIỆU TỪ FILE RA FONTEND
        /*
        Excel format:
        0 matohop
        1 mon1
        2 mon2
        3 mon3
        4 tentohop
        */
        public List<TohopMonthi> importFromExcel(String filePath) throws Exception {

            List<TohopMonthi> list = new ArrayList<>();

            try (FileInputStream fis = new FileInputStream(filePath);
                 XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheetAt(0);

                for (Row row : sheet) {

                    if (row.getRowNum() == 0 || isRowEmpty(row)) {
                        continue;
                    }

                    try {
                        TohopMonthi entity = parseRow(row);
                        save(entity);
                        list.add(entity);

                    } catch (Exception e) {
                        System.err.println("Lỗi dòng: "
                                + (row.getRowNum() + 1));
                    }
                }
            }
            return list;
        }

    // ================= DELETE =================
    public boolean deleteById(Integer id) {

        Transaction tx = null;

        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();

            TohopMonthi existing =
                    session.get(TohopMonthi.class, id);

            if (existing == null) {
                tx.commit();
                return false;
            }

            session.remove(existing);
            tx.commit();
            return true;

        } catch (Exception e) {
            rollback(tx);
            throw e;
        }
    }

    private void rollback(Transaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }
}
