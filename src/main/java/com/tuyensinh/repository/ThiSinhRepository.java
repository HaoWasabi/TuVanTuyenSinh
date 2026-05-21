package com.tuyensinh.repository;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.ThiSinh;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ThiSinhRepository {

    // Xem tất cả thí sinh
    public List<ThiSinh> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from ThiSinh t where t.status = 'active'", ThiSinh.class).list();
        }
    }

    // Tìm kiếm thí sinh theo CCCD (chính xác)
    public Optional<ThiSinh> findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            ThiSinh result = session.createQuery("from ThiSinh t where t.cccd = :cccd and t.status = 'active'", ThiSinh.class)
                    .setParameter("cccd", cccd)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(result);
        }
    }

    // Tìm kiếm thí sinh theo Họ Tên (tìm kiếm gần đúng)
    public List<ThiSinh> findByHoTen(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Tìm kiếm chuỗi keyword xuất hiện trong 'Họ', hoặc 'Tên', hoặc ghép cả 'Họ' và 'Tên'
            String hql = "from ThiSinh t where (lower(t.ho) like lower(:keyword) " +
                         "or lower(t.ten) like lower(:keyword) " +
                         "or lower(concat(t.ho, ' ', t.ten)) like lower(:keyword)) and t.status = 'active'";
            return session.createQuery(hql, ThiSinh.class)
                    .setParameter("keyword", "%" + keyword + "%")
                    .list();
        }
    }

    // Lấy thí sinh theo ID
    public Optional<ThiSinh> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            ThiSinh result = session.createQuery("from ThiSinh t where t.id = :id and t.status = 'active'", ThiSinh.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(result);
        }
    }

    // Cập nhật thông tin thí sinh
    public ThiSinh update(ThiSinh thiSinh) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            ThiSinh merged = (ThiSinh) session.merge(thiSinh);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }

    // Thêm mới thí sinh vào cơ sở dữ liệu
    public ThiSinh save(ThiSinh thiSinh) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(thiSinh);
            tx.commit();
            return thiSinh;
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }

    public boolean deleteByCccd(String cccd) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            ThiSinh existing = session.createQuery("from ThiSinh t where t.cccd = :cccd", ThiSinh.class)
                    .setParameter("cccd", cccd)
                    .setMaxResults(1)
                    .uniqueResult();

            if (existing == null) {
                tx.commit();
                return false;
            }

            // CHUYỂN SANG XÓA MỀM THÍ SINH
            existing.setStatus("INACTIVE");
            session.merge(existing);

            // XÓA MỀM TÀI KHOẢN NGƯỜI DÙNG TƯƠNG ỨNG (NẾU CÓ)
            com.tuyensinh.model.User user = session.createQuery("from User u where u.studentCccd = :cccd", com.tuyensinh.model.User.class)
                    .setParameter("cccd", cccd)
                    .setMaxResults(1)
                    .uniqueResult();
            if (user != null) {
                user.setStatus("INACTIVE");
                session.merge(user);
            }

            tx.commit();
            return true;
        } catch (Exception ex) {
            rollbackQuietly(tx);
            throw ex;
        }
    }

    /**
     * Import danh sách Thí sinh từ file Excel
     * Tự động lọc các cột cần thiết và bỏ qua các cột điểm/đánh giá
     */
    public List<ThiSinh> importFromExcel(String filePath) throws IOException {
        List<ThiSinh> importedList = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            int rowStart = 1; // Bỏ qua header
            
            for (Row row : sheet) {
                if (row.getRowNum() < rowStart) {
                    continue;
                }
                
                if (isRowEmpty(row)) {
                    continue;
                }
                
                try {
                    ThiSinh thiSinh = parseExcelRow(row);
                    saveWithoutTransaction(thiSinh);
                    importedList.add(thiSinh);
                } catch (Exception ex) {
                    System.err.println("Lỗi import dòng " + (row.getRowNum() + 1) + " (Có thể do trùng CCCD): " + ex.getMessage());
                }
            }
        }
        
        return importedList;
    }

	private ThiSinh parseExcelRow(Row row) {
		ThiSinh ts = new ThiSinh();

		String cccd = truncateString(getCellAsString(row, 0), 20);
		ts.setCccd(cccd);

		ts.setSobaodanh(truncateString(getCellAsString(row, 1), 45));
		ts.setHo(truncateString(getCellAsString(row, 2), 100));
		ts.setTen(truncateString(getCellAsString(row, 3), 100));

		String ngaySinh = getCellAsString(row, 4);
		ts.setNgaySinh(truncateString(ngaySinh, 45));

		ts.setGioiTinh(truncateString(getCellAsString(row, 5), 10));
		ts.setEmail(truncateString(getCellAsString(row, 6), 100));
		ts.setDienThoai(truncateString(getCellAsString(row, 7), 20));
		ts.setNoiSinh(truncateString(getCellAsString(row, 8), 45));
		ts.setDoiTuong(truncateString(getCellAsString(row, 9), 45));
		ts.setKhuVuc(truncateString(getCellAsString(row, 10), 45));

		ts.setUpdatedAt(LocalDate.now());

		// Password là 8 chữ số ngày sinh, bỏ dấu "/" hoặc "-"
		// Ví dụ: "01/07/2006" → "01072006"
		String password = (ngaySinh != null && !ngaySinh.isEmpty())
				? truncateString(ngaySinh.replace("/", "").replace("-", ""), 8)
				: cccd; // fallback về CCCD nếu không có ngày sinh
		ts.setPassword(password);

		return ts;
	}

    private String getCellAsString(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return null;
        }
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        // Quét cột 1 (CCCD) và 2 (Họ Tên) xem có dữ liệu không
        Cell cellCccd = row.getCell(1);
        Cell cellHoTen = row.getCell(2);
        boolean emptyCccd = (cellCccd == null || cellCccd.getCellType().toString().isEmpty());
        boolean emptyHoTen = (cellHoTen == null || cellHoTen.getCellType().toString().isEmpty());
        return emptyCccd && emptyHoTen;
    }

    private String truncateString(String value, int maxLength) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }

    private void saveWithoutTransaction(ThiSinh thiSinh) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(thiSinh);
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